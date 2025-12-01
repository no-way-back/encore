package com.nowayback.reward.domain.reward.repository;

import com.nowayback.reward.domain.reward.entity.Rewards;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RewardRepository {
    Rewards save(Rewards reward);
    List<Rewards> saveAll(List<Rewards> rewards);

    Optional<Rewards> findById(UUID rewardId);

}