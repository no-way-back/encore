package com.nowayback.reward.application.reward.repository;

import com.nowayback.reward.domain.reward.entity.Rewards;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RewardRepository {
    Rewards save(Rewards reward);
    Optional<Rewards> findById(UUID rewardId);
    List<Rewards> findAvailableReward(UUID projectId);
    Optional<Rewards> findByIdWithLock(UUID rewardId);
}