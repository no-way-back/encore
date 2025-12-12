package com.nowayback.reward.application.outbox.repository;

import com.nowayback.reward.domain.outbox.Outbox;
import com.nowayback.reward.domain.outbox.vo.OutboxStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxRepository {
    Outbox save(Outbox outbox);
    List<Outbox> findRetryableEvents(int maxRetries);
    void deletePublishedEventsBefore(LocalDateTime threshold);
    List<Outbox> findByStatus(OutboxStatus status);
}

