package com.nowayback.reward.infrastructure.repository.inbox;

import com.nowayback.reward.application.inbox.repository.InboxRepository;
import com.nowayback.reward.domain.inbox.entity.Inbox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InboxRepositoryImpl implements InboxRepository {

    private final InboxJpaRepository inboxJpaRepository;

    public Inbox save(Inbox inbox) {
        return inboxJpaRepository.save(inbox);
    }
}
