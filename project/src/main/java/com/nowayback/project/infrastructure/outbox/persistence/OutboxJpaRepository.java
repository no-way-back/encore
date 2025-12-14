package com.nowayback.project.infrastructure.outbox.persistence;

import com.nowayback.project.domain.outbox.Outbox;
import com.nowayback.project.domain.outbox.vo.OutboxStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxJpaRepository extends JpaRepository<Outbox, UUID> {

    List<Outbox> findAllByStatus(OutboxStatus outboxStatus);
}
