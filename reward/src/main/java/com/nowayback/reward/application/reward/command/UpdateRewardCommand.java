package com.nowayback.reward.application.reward.command;

import com.nowayback.reward.domain.reward.vo.RewardType;
import com.nowayback.reward.presentation.reward.dto.request.UpdateRewardRequest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record UpdateRewardCommand(
        UUID rewardId,
        String name,
        String description,
        Integer price,
        Integer stockQuantity,
        Integer shippingFee,
        Integer freeShippingAmount,
        Integer purchaseLimitPerPerson,
        RewardType rewardType,
        List<UpdateRewardOptionCommand> options
) {
    public static UpdateRewardCommand from(UUID rewardId, UpdateRewardRequest request) {
        return new UpdateRewardCommand(
                rewardId,
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity(),
                request.shippingFee(),
                request.freeShippingAmount(),
                request.purchaseLimitPerPerson(),
                request.rewardType(),
                request.options() != null ?
                        request.options().stream()
                                .map(UpdateRewardOptionCommand::from)
                                .collect(Collectors.toList())
                        : null
        );
    }
}