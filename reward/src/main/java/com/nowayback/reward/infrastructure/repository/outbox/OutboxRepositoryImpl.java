package com.nowayback.reward.infrastructure.repository.outbox;

import com.nowayback.reward.application.outbox.repository.OutboxRepository;
import com.nowayback.reward.domain.outbox.entity.Outbox;
import com.nowayback.reward.domain.outbox.vo.OutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public Outbox save(Outbox outbox) {
        return outboxJpaRepository.save(outbox);
    }

    @Override
    public List<Outbox> findRetryableEvents(int maxRetries) {
        return outboxJpaRepository.findRetryableEvents(maxRetries);
    }

    @Override
    public void deletePublishedEventsBefore(LocalDateTime threshold) {
        outboxJpaRepository.deletePublishedEventsBefore(threshold);
    }

    @Override
    public List<Outbox> findByStatus(OutboxStatus status) {
        return outboxJpaRepository.findByStatus(status);
    }
}