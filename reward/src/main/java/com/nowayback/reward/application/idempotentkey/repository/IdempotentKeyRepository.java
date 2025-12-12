package com.nowayback.reward.application.idempotentkey.repository;

import com.nowayback.reward.domain.event.IdempotentKeys;

public interface IdempotentKeyRepository {
    IdempotentKeys save(IdempotentKeys event);
    boolean existsByEventId(String eventId);
}