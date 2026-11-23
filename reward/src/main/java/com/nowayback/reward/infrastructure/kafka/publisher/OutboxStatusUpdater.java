package com.nowayback.reward.infrastructure.kafka.publisher;

import com.nowayback.reward.application.outbox.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxStatusUpdater {

    private final OutboxRepository outboxRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsPublished(UUID outboxId) {
        outboxRepository.findById(outboxId)
                .ifPresent(outbox -> {
                    outbox.markAsPublished();
                    outboxRepository.save(outbox);
                });
    }
}