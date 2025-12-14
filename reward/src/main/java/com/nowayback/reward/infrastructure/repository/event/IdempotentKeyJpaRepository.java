package com.nowayback.reward.infrastructure.repository.event;

import com.nowayback.reward.domain.idempotentkey.IdempotentKeys;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IdempotentKeyJpaRepository extends JpaRepository<IdempotentKeys, String> {
    boolean existsById(UUID eventId);
}