package com.nowayback.project.application.outbox.event;

import com.nowayback.project.domain.outbox.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventListener {

    private final OutboxRepository outboxRepository;

    private final OutboxKafkaPublisher outboxKafkaPublisher;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createOutbox(OutboxEvent outboxEvent) {
        log.info("[OutboxEventListener.createOutbox] outboxEvent={}", outboxEvent);
        outboxRepository.save(outboxEvent.getOutbox());
    }

    @Async("messagePublishEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(OutboxEvent outboxEvent) {
        log.info("[OutboxEventListener.publish] outboxId={}", outboxEvent.getOutbox().getId());

        outboxKafkaPublisher.publishImmediately(outboxEvent.getOutbox());
    }
}
