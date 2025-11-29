package com.nowayback.funding.infrastructure.funding;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nowayback.funding.domain.funding.entity.Outbox;
import com.nowayback.funding.domain.funding.entity.OutboxStatus;

public interface OutboxJpaRepository extends JpaRepository<Outbox, UUID> {

	List<Outbox> findAllByStatus(OutboxStatus status);
}