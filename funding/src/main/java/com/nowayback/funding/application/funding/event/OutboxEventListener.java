package com.nowayback.funding.application.funding.event;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;

import java.util.concurrent.TimeUnit;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.nowayback.funding.domain.funding.event.OutboxEventCreated;
import com.nowayback.funding.domain.service.OutboxService;
import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.funding.entity.Outbox;
import com.nowayback.funding.domain.funding.repository.OutboxRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxEventListener {

	private final OutboxRepository outboxRepository;
	private final OutboxService outboxService;
	private final KafkaTemplate<String, String> kafkaTemplate;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Async("outboxTaskExecutor")
	public void handleRewardCancellationOutboxEvent(OutboxEventCreated event) {

		Outbox outboxEvent = outboxRepository.findById(event.eventId())
			.orElseThrow(() -> new FundingException(OUTBOX_EVENT_NOT_FOUND));

		try {
			kafkaTemplate.send("reward-cancellation", outboxEvent.getPayload())
				.get(3, TimeUnit.SECONDS);

			outboxService.markAsPublished(outboxEvent.getId());

			log.info("Outbox 이벤트 발행 성공: eventId={}", event.eventId());
		} catch (Exception e) {
			log.warn("Outbox 이벤트 발행 실패 (스케줄러가 재시도): eventId={}", event.eventId(), e);
		}
	}
}
