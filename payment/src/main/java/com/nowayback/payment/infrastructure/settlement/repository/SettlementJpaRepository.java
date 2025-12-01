package com.nowayback.payment.infrastructure.settlement.repository;

import com.nowayback.payment.domain.settlement.entity.Settlement;
import com.nowayback.payment.domain.settlement.vo.ProjectId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SettlementJpaRepository extends JpaRepository<Settlement, UUID> {
    Optional<Settlement> findByProjectId(ProjectId projectId);
    boolean existsByProjectId(ProjectId projectId);
}
