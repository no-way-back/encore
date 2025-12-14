package com.nowayback.funding.infrastructure.schedule;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nowayback.funding.application.outbox.event.KafkaEventPublisher;
import com.nowayback.funding.application.outbox.service.OutboxService;
import com.nowayback.funding.domain.outbox.entity.Outbox;
import com.nowayback.funding.infrastructure.aop.DistributedLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxScheduler {

	private final OutboxService outboxService;
	private final KafkaEventPublisher kafkaEventPublisher;

	@Scheduled(fixedDelay = 300000)
	@DistributedLock(
		key = "'scheduler:retryPendingEvents'",
		leaseTime = 4,
		timeUnit = TimeUnit.MINUTES,
		useTransaction = false
	)
	public void retryPendingEvents() {
		log.info("Outbox 재시도 스케줄러 실행");

		try {
			List<Outbox> pendingEvents = outboxService.getPendingEvents();

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

						outboxService.markAsFailed(event.getId());

						// TODO: 알림 발송 (Slack, Email 등)
						// - 이벤트 ID
						// - 이벤트 타입
						// - 재시도 횟수
						// - 수동 복구 필요 메시지

						continue;
					}

					kafkaEventPublisher.publish(event);

					outboxService.markAsPublished(event.getId());

					log.info("Outbox 이벤트 재시도 성공 - eventId: {}", event.getId());

				} catch (Exception e) {
					log.warn("Outbox 이벤트 재시도 실패 - eventId: {}, retryCount: {}",
						event.getId(), event.getRetryCount(), e);

					outboxService.incrementRetryCount(event.getId());
				}
			}

			log.info("Outbox 재시도 스케줄러 완료");

		} catch (Exception e) {
			log.error("Outbox 재시도 스케줄러 실행 중 오류 발생", e);
		}
	}
}
