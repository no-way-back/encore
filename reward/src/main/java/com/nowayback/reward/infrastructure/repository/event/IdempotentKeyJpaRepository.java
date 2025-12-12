package com.nowayback.reward.infrastructure.repository.event;

import com.nowayback.reward.domain.event.IdempotentKeys;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotentKeyJpaRepository extends JpaRepository<IdempotentKeys, String> {
    boolean existsByEventId(String eventId);
}