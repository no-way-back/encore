package com.nowayback.reward.application.inbox.repository;

import com.nowayback.reward.domain.inbox.entity.Inbox;

public interface InboxRepository {
    Inbox save(Inbox inbox);
}
