package com.nowayback.funding.domain.funding.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.nowayback.funding.domain.funding.entity.Outbox;

public interface OutboxRepository {

	Outbox save(Outbox outbox);

	Optional<Outbox> findById(UUID id);

	List<Outbox> findPendingEvents();

	void delete(Outbox outbox);
}
