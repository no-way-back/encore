package com.nowayback.reward.infrastructure.scheduler;

import com.nowayback.reward.application.outbox.event.OutboxPublisher;
import com.nowayback.reward.application.outbox.repository.OutboxRepository;
import com.nowayback.reward.domain.outbox.Outbox;
import com.nowayback.reward.domain.outbox.vo.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPollingScheduler {

    private final OutboxRepository outboxRepository;
    private final OutboxPublisher outboxPublisher;

    private static final int MAX_RETRY_COUNT = 5;

    @Scheduled(fixedDelay = 1000)
    public void pollAndPublish() {
        List<Outbox> retryableEvents = outboxRepository.findRetryableEvents(MAX_RETRY_COUNT);

        if (retryableEvents.isEmpty()) {
            return;
        }

        log.info("미발행 이벤트 {}건 처리 시작", retryableEvents.size());

        for (Outbox outbox : retryableEvents) {
            outboxPublisher.publishWithRetry(outbox);
        }
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupOldPublishedEvents() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(7);
        outboxRepository.deletePublishedEventsBefore(threshold);
        log.info("7일 이전 발행 완료 이벤트 정리 완료");
    }

    @Scheduled(cron = "0 0 * * * *")
    public void monitorFailedEvents() {
        List<Outbox> failedEvents = outboxRepository.findByStatus(OutboxStatus.FAILED);

        if (!failedEvents.isEmpty()) {
            log.warn("발행 실패한 이벤트 {}건 존재 - 수동 확인 필요",
                    failedEvents.size());

            for (Outbox outbox : failedEvents) {
                log.warn("실패 이벤트 - id: {}, type: {}, projectId: {}, error: {}",
                        outbox.getId(),
                        outbox.getEventType(),
                        outbox.getAggregateId(),
                        outbox.getErrorMessage());
            }
        }
    }
}