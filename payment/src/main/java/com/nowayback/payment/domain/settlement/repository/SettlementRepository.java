package com.nowayback.payment.domain.settlement.repository;

import com.nowayback.payment.domain.settlement.entity.Settlement;
import com.nowayback.payment.domain.settlement.vo.ProjectId;

import java.util.Optional;

public interface SettlementRepository {
    Settlement save(Settlement settlement);
    Optional<Settlement> findByProjectId(ProjectId projectId);
    boolean existsByProjectId(ProjectId projectId);
}
