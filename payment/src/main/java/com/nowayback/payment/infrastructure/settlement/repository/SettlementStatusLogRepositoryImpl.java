package com.nowayback.payment.infrastructure.settlement.repository;

import com.nowayback.payment.domain.settlement.entity.SettlementStatusLog;
import com.nowayback.payment.domain.settlement.repository.SettlementStatusLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SettlementStatusLogRepositoryImpl implements SettlementStatusLogRepository {

    private final SettlementStatusLogJpaRepository jpaRepository;

    @Override
    public SettlementStatusLog save(SettlementStatusLog settlementStatusLog) {
        return jpaRepository.save(settlementStatusLog);
    }

    @Override
    public Page<SettlementStatusLog> findAllBySettlementId(UUID settlementId, Pageable pageable) {
        return jpaRepository.findAllBySettlementId(settlementId, pageable);
    }
}
