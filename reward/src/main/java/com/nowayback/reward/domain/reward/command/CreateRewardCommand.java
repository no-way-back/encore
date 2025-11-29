package com.nowayback.reward.domain.reward.command;

import com.nowayback.reward.domain.reward.vo.RewardType;
import com.nowayback.reward.infrastructure.kafka.dto.project.request.RewardCreateRequest;

import java.util.List;
import java.util.UUID;

public record CreateRewardCommand(
        UUID projectId,
        String name,
        String description,
        Integer price,
        Integer stockQuantity,
        Integer shippingFee,
        Integer freeShippingAmount,
        Integer purchaseLimitPerPerson,
        RewardType rewardType,
        List<CreateRewardOptionCommand> options
) {
    public static CreateRewardCommand from(UUID projectId, RewardCreateRequest request) {
        return new CreateRewardCommand(
                projectId,
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity(),
                request.shippingFee(),
                request.freeShippingAmount(),
                request.purchaseLimitPerPerson(),
                request.rewardType(),
                CreateRewardOptionCommand.from(request.options())
        );
    }
}