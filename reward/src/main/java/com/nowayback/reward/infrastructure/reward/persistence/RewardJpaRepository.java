package com.nowayback.reward.infrastructure.reward.persistence;

import com.nowayback.reward.domain.reward.entity.Rewards;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RewardJpaRepository extends JpaRepository<Rewards, UUID> {
}