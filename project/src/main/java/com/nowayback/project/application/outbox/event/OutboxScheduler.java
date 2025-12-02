package com.nowayback.project.application.outbox.event;


import com.nowayback.project.domain.outbox.Outbox;
import com.nowayback.project.domain.outbox.repository.OutboxRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final OutboxKafkaPublisher outboxKafkaPublisher;

    @Scheduled(
        fixedDelay = 10,
        initialDelay = 5,
        timeUnit = TimeUnit.SECONDS,
        scheduler = "messagePublishPendingEventExecutor"
    )
    public void retryPendingEvents() {
        log.info("[OutboxScheduler.retryPendingEvents] Outbox 재시도 스케줄러 시작");

        List<Outbox> pendingEvents = outboxRepository.findPendingEvents();

        if (pendingEvents.isEmpty()) {
            log.info("[OutboxScheduler.retryPendingEvents] 재시도할 Outbox 이벤트가 없습니다.");
            return;
        }

        log.info("[OutboxScheduler.retryPendingEvents] 재시도 대상 Outbox 이벤트: {}건",
            pendingEvents.size());


        int successCount = 0;
        int failureCount = 0;

        for (Outbox outbox : pendingEvents) {
            boolean success = outboxKafkaPublisher.publishWithRetry(outbox);

            if (success) {
                successCount++;
            } else {
                failureCount++;
            }
        }

        log.info("[OutboxScheduler.retryPendingEvents] 성공: {}건, 실패: {}건", successCount, failureCount);
    }
}
