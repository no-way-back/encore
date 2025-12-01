package com.nowayback.funding.infrastructure.funding;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.nowayback.funding.domain.funding.entity.Outbox;
import com.nowayback.funding.domain.funding.entity.OutboxStatus;
import com.nowayback.funding.domain.funding.repository.OutboxRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

	private final OutboxJpaRepository jpaRepository;

	@Override
	public Outbox save(Outbox outbox) {
		return jpaRepository.save(outbox);
	}

	@Override
	public Optional<Outbox> findById(UUID id) {
		return jpaRepository.findById(id);
	}

	@Override
	public List<Outbox> findPendingEvents() {
		return jpaRepository.findAllByStatus(OutboxStatus.PENDING);
	}

	@Override
	public void delete(Outbox outbox) {
		jpaRepository.delete(outbox);
	}
}
