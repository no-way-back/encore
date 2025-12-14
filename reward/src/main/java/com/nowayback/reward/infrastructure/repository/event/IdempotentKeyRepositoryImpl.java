package com.nowayback.reward.infrastructure.repository.event;

import com.nowayback.reward.application.idempotentkey.repository.IdempotentKeyRepository;
import com.nowayback.reward.domain.idempotentkey.IdempotentKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class IdempotentKeyRepositoryImpl implements IdempotentKeyRepository {

    private final IdempotentKeyJpaRepository jpaRepository;

    @Override
    public IdempotentKeys save(IdempotentKeys event) {
        return jpaRepository.save(event);
    }

    @Override
    public boolean existsById(UUID eventId) {
        return jpaRepository.existsById(eventId);
    }
}