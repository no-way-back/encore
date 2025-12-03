package com.nowayback.payment.infrastructure.settlement.repository;

import com.nowayback.payment.domain.settlement.entity.Settlement;
import com.nowayback.payment.domain.settlement.repository.SettlementRepository;
import com.nowayback.payment.domain.settlement.vo.ProjectId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SettlementRepositoryImpl implements SettlementRepository {

    private final SettlementJpaRepository jpaRepository;

    @Override
    public Settlement save(Settlement settlement) {
        return jpaRepository.save(settlement);
    }

    @Override
    public Optional<Settlement> findByProjectId(ProjectId projectId) {
        return jpaRepository.findByProjectId(projectId);
    }

    @Override
    public boolean existsByProjectId(ProjectId projectId) {
        return jpaRepository.existsByProjectId(projectId);
    }
}
