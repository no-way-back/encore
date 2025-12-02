package com.nowayback.project.application.outbox.event;

import com.nowayback.project.domain.outbox.Outbox;
import com.nowayback.project.domain.outbox.repository.OutboxRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxKafkaPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishImmediately(Outbox outbox) {
        try {
            sendToKafka(outbox);

            outbox.markAsPublished();
            outboxRepository.save(outbox);

            log.info("[OutboxKafkaPublisher.publishImmediately] 발행 성공 outboxId={}, topic={}",
                outbox.getId(), outbox.getEventType().getTopic());

        } catch (Exception e) {
            log.info("[OutboxKafkaPublisher.publishImmediately] 발행 실패, 폴링에서 재시도 outboxId={}",
                outbox.getId());
        }
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean publishWithRetry(Outbox outbox) {
        if (outbox.getRetryCount() >= 5) {
            log.error("[OutboxKafkaPublisher.publishWithRetry] 재시도 한계 초과 outboxId={}, 수동 처리 필요", outbox.getId());
            outbox.markAsFailed();
            outboxRepository.save(outbox);
            // TODO: 알림 발송 (Slack, Email)
            return false;
        }

        try {
            sendToKafka(outbox);

            // 성공 처리
            outbox.markAsPublished();
            outboxRepository.save(outbox);

            log.info("[OutboxKafkaPublisher.publishWithRetry] 폴링 발행 성공 outboxId={}, retryCount={}",
                outbox.getId(), outbox.getRetryCount());
            return true;

        } catch (Exception e) {
            // 실패 처리
            outbox.incrementRetryCount();
            outboxRepository.save(outbox);

            log.warn("[OutboxKafkaPublisher.publishWithRetry] 폴링 발행 실패 outboxId={}, retryCount={}/{}",
                outbox.getId(), outbox.getRetryCount(), 5);
            return false;
        }
    }

    private void sendToKafka(Outbox outbox) throws Exception {
        kafkaTemplate.send(
            outbox.getEventType().getTopic(),
            outbox.getAggregateId().toString(),
            outbox.getPayload()
        ).get(3, TimeUnit.SECONDS);
    }
}
