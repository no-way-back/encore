package com.nowayback.reward.infrastructure.repository.inbox;

import com.nowayback.reward.application.inbox.repository.InboxRepository;
import com.nowayback.reward.domain.inbox.entity.Inbox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InboxRepositoryImpl implements InboxRepository {

    private final InboxJpaRepository inboxJpaRepository;

    @Override
    public Inbox save(Inbox inbox) {
        return inboxJpaRepository.save(inbox);
    }

    @Override
    public Optional<Inbox> findById(UUID id) {
        return inboxJpaRepository.findById(id);
    }

    @Override
    public boolean saveIfAbsent(Inbox inbox) {
        int inserted = inboxJpaRepository.insertIfAbsent(
                inbox.getId(),
                inbox.getEventType().name(),
                inbox.getAggregateId(),
                inbox.getPayload(),
                inbox.getReceivedAt()
        );
        return inserted == 1;
    }

    @Override
    public Optional<Inbox> findByIdForUpdate(UUID id) {
        return inboxJpaRepository.findByIdForUpdate(id);
    }
}
