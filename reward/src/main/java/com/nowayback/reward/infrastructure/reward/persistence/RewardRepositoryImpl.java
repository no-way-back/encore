package com.nowayback.reward.infrastructure.reward.persistence;

import com.nowayback.reward.domain.reward.entity.Rewards;
import com.nowayback.reward.domain.reward.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}