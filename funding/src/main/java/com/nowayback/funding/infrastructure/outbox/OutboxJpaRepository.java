package com.nowayback.funding.infrastructure.outbox;

import com.nowayback.funding.domain.outbox.entity.Outbox;
import com.nowayback.funding.domain.outbox.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxJpaRepository extends JpaRepository<Outbox, UUID> {

	List<Outbox> findAllByStatus(OutboxStatus status);
}