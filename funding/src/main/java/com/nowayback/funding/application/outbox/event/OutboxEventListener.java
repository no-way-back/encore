package com.nowayback.funding.application.outbox.event;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;
import static com.nowayback.funding.infrastructure.config.KafkaTopics.*;

import java.util.concurrent.TimeUnit;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.nowayback.funding.domain.event.OutboxEventCreated;
import com.nowayback.funding.application.outbox.service.OutboxService;
import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.outbox.entity.Outbox;
import com.nowayback.funding.domain.outbox.repository.OutboxRepository;

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
	public void handleOutboxEvent(OutboxEventCreated event) {

		Outbox outboxEvent = outboxRepository.findById(event.eventId())
			.orElseThrow(() -> new FundingException(OUTBOX_EVENT_NOT_FOUND));

		try {
			String topic = getTopicName(outboxEvent.getEventType());

			kafkaTemplate.send(topic, outboxEvent.getPayload())
				.get(3, TimeUnit.SECONDS);

			outboxService.markAsPublished(outboxEvent.getId());

			log.info("Outbox 이벤트 발행 성공: eventId={}, topic={}", event.eventId(), topic);
		} catch (Exception e) {
			log.warn("Outbox 이벤트 발행 실패 (스케줄러가 재시도): eventId={}", event.eventId(), e);
		}
	}

	private String getTopicName(String eventType) {
		return switch (eventType) {
			case "FUNDING_FAILED" -> FUNDING_FAILED;
			case "FUNDING_CANCELLED" -> FUNDING_CANCELLED;
			case "FUNDING_COMPLETED" -> FUNDING_COMPLETED;
			default -> "funding-events";
		};
	}
}
