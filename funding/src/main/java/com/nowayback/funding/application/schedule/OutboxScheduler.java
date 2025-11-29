package com.nowayback.funding.application.schedule;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nowayback.funding.application.funding.service.OutboxService;
import com.nowayback.funding.domain.funding.entity.Outbox;
import com.nowayback.funding.domain.funding.repository.OutboxRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxScheduler {

	private final OutboxRepository outboxRepository;
	private final OutboxService outboxService;
	private final KafkaTemplate<String, String> kafkaTemplate;

	/**
	 * 5분마다 미발행 이벤트 재시도
	 */
	@Scheduled(fixedDelay = 300000)
	@Transactional
	public void retryPendingEvents() {
		log.info("Outbox 재시도 스케줄러 시작");

		List<Outbox> pendingEvents = outboxRepository.findPendingEvents();

		if (pendingEvents.isEmpty()) {
			log.debug("재시도할 Outbox 이벤트가 없습니다.");
			return;
		}

		log.info("재시도 대상 Outbox 이벤트: {}건", pendingEvents.size());

		for (Outbox event : pendingEvents) {
			try {
				if (event.getRetryCount() >= 5) {
					log.error("Outbox 이벤트 최대 재시도 횟수 초과 - eventId: {}, 수동 처리 필요",
						event.getId());
					// TODO: 알림 발송 (Slack, Email 등)
					continue;
				}

				kafkaTemplate.send(getTopicName(event.getEventType()), event.getPayload())
					.get(3, TimeUnit.SECONDS);

				outboxService.markAsPublished(event.getId());

				log.info("Outbox 이벤트 재시도 성공 - eventId: {}", event.getId());

			} catch (Exception e) {
				log.warn("Outbox 이벤트 재시도 실패 - eventId: {}, retryCount: {}",
					event.getId(), event.getRetryCount(), e);

				outboxService.incrementRetryCount(event.getId());
			}
		}
	}

	private String getTopicName(String eventType) {
		return switch (eventType) {
			case "REWARD_CANCELLATION_REQUESTED" -> "reward-cancellation";
			case "FUNDING_COMPLETED" -> "funding-completed";
			case "FUNDING_CANCELLED" -> "funding-cancelled";
			default -> "funding-events";
		};
	}
}
