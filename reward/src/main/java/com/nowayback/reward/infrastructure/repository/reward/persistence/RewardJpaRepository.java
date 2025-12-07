package com.nowayback.reward.infrastructure.repository.reward.persistence;

import com.nowayback.reward.domain.reward.entity.Rewards;
import com.nowayback.reward.domain.vo.FundingId;
import com.nowayback.reward.domain.vo.ProjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RewardJpaRepository extends JpaRepository<Rewards, UUID> {

    @Query("SELECT r FROM Rewards r JOIN FETCH r.optionList WHERE r.projectId = :projectId AND r.status = 'AVAILABLE'")
    List<Rewards> findAvailableReward(ProjectId projectId);
}