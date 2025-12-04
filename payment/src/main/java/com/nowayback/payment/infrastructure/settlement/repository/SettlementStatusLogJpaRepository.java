package com.nowayback.payment.infrastructure.settlement.repository;

import com.nowayback.payment.domain.settlement.entity.SettlementStatusLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SettlementStatusLogJpaRepository extends JpaRepository<SettlementStatusLog, UUID> {
    Page<SettlementStatusLog> findAllBySettlementId(UUID settlementId, Pageable pageable);
}
