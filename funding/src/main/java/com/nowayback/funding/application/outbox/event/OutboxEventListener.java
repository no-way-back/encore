package com.nowayback.funding.application.outbox.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.nowayback.funding.domain.event.OutboxEventCreated;
import com.nowayback.funding.application.outbox.service.OutboxService;
import com.nowayback.funding.domain.outbox.entity.Outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxEventListener {

	private final OutboxService outboxService;
	private final KafkaEventPublisher kafkaEventPublisher;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Async("outboxTaskExecutor")
	public void handleOutboxEvent(OutboxEventCreated event) {

		Outbox outboxEvent = outboxService.findById(event.eventId());

		try {
			kafkaEventPublisher.publish(outboxEvent);

			outboxService.markAsPublished(outboxEvent.getId());

			log.info("Outbox 이벤트 발행 성공: eventId={}, topic={}", event.eventId(), outboxEvent.getEventType());
		} catch (Exception e) {
			log.warn("Outbox 이벤트 발행 실패 (스케줄러가 재시도): eventId={}", event.eventId(), e);
		}
	}
}
