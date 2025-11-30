package com.nowayback.project.infrastructure.outbox.persistence;

import com.nowayback.project.domain.outbox.Outbox;
import com.nowayback.project.domain.outbox.repository.OutboxRepository;
import com.nowayback.project.domain.outbox.vo.OutboxStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository outboxJpaRepository;
    @Override
    public Outbox save(Outbox outbox) {
        return outboxJpaRepository.save(outbox);
    }

    @Override
    public List<Outbox> findPendingEvents() {
        return outboxJpaRepository.findAllByStatus(OutboxStatus.PENDING);
    }
}
