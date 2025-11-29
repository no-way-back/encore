package com.nowayback.reward.infrastructure.kafka.dto.project.request;

import com.nowayback.reward.domain.reward.vo.RewardType;

import java.util.List;

public record RewardCreateRequest(
        String name,
        String description,
        Integer price,
        Integer stockQuantity,
        Integer shippingFee,
        Integer freeShippingAmount,
        Integer purchaseLimitPerPerson,
        RewardType rewardType,
        List<RewardOptionCreateRequest> options
) {
}