package com.nowayback.reward.application.reward.command;

import com.nowayback.reward.domain.reward.vo.RewardType;

import java.util.List;

public record RewardCreateCommand(
        String name,
        String description,
        Long price,
        Integer stockQuantity,
        Integer shippingFee,
        Integer freeShippingAmount,
        Integer purchaseLimitPerPerson,
        RewardType rewardType,
        List<RewardOptionCreateCommand> options
) {
}
