package com.nowayback.reward.application.idempotentkey.repository;

import com.nowayback.reward.domain.idempotentkey.IdempotentKeys;

import java.util.UUID;

public interface IdempotentKeyRepository {
    IdempotentKeys save(IdempotentKeys event);
    boolean existsById(UUID eventId);
}