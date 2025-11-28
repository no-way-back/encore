package com.nowayback.reward.domain.reward.repository;


import com.nowayback.reward.domain.reward.entity.RewardOptions;

import java.util.List;

public interface RewardOptionRepository {
    RewardOptions save(RewardOptions option);
    List<RewardOptions> saveAll(List<RewardOptions> options);
}