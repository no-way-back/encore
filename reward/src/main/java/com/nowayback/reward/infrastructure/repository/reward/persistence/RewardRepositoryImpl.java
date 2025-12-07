package com.nowayback.reward.infrastructure.repository.reward.persistence;

import com.nowayback.reward.domain.reward.entity.Rewards;
import com.nowayback.reward.domain.reward.repository.RewardRepository;
import com.nowayback.reward.domain.vo.ProjectId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RewardRepositoryImpl implements RewardRepository {

    private final RewardJpaRepository jpaRepository;

    @Override
    public Rewards save(Rewards reward) {
        return jpaRepository.save(reward);
    }

    @Override
    public List<Rewards> saveAll(List<Rewards> rewards) {
        return jpaRepository.saveAll(rewards);
    }

    @Override
    public Optional<Rewards> findById(UUID rewardId) {
        return jpaRepository.findById(rewardId);
    }

    @Override
    public List<Rewards> findAvailableReward(UUID projectId) {
        return jpaRepository.findAvailableReward(ProjectId.of(projectId));
    }
}