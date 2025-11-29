package com.nowayback.reward.domain.reward.command;

import com.nowayback.reward.domain.reward.handler.command.RewardCreateCommand;
import com.nowayback.reward.domain.reward.vo.RewardType;

import java.util.List;
import java.util.UUID;

public record CreateRewardCommand(
        UUID projectId,
        UUID creatorId,
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
    public static CreateRewardCommand from(UUID projectId, UUID creatorId, RewardCreateCommand command) {
        return new CreateRewardCommand(
                projectId,
                creatorId,
                command.name(),
                command.description(),
                command.price(),
                command.stockQuantity(),
                command.shippingFee(),
                command.freeShippingAmount(),
                command.purchaseLimitPerPerson(),
                command.rewardType(),
                CreateRewardOptionCommand.from(command.options())
        );
    }
}