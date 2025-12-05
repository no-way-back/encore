package com.nowayback.payment.domain.settlement.repository;

import com.nowayback.payment.domain.settlement.entity.SettlementStatusLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SettlementStatusLogRepository {
    SettlementStatusLog save(SettlementStatusLog settlementStatusLog);
    Page<SettlementStatusLog> findAllBySettlementId(UUID settlementId, Pageable pageable);
}
