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
			case "FUNDING_FAILED" -> FUNDING_FAILED; // 리워드 - 재고 복구
			case "FUNDING_REFUND" -> FUNDING_REFUND; // 리워드 - 재고 복구
			case "FUNDING_COMPLETED" -> FUNDING_COMPLETED; // 리워드 - QR 생성
			case "PROJECT_FUNDING_CREATED_FAILED" -> PROJECT_FUNDING_CREATED_FAILED; // 프로젝트 - 엔티티 생성 실패 -> 복구에 필요
			case "PROJECT_FUNDING_SUCCESS" -> PROJECT_FUNDING_SUCCESS; // 프로젝트, 리워드 - 상태변경 / 리워드 제공
			case "PROJECT_FUNDING_FAILED" -> PROJECT_FUNDING_FAILED; // 프로젝트, 결제 : 상태변경 / 전액 환불 진행
			default -> "funding-events";
		};
	}
}
