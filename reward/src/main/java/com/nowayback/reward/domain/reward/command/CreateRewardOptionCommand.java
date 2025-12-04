package com.nowayback.reward.domain.reward.command;

import com.nowayback.reward.application.reward.command.RewardOptionCreateCommand;

import java.util.List;

public record CreateRewardOptionCommand(
        String name,
        Long additionalPrice,
        Integer stockQuantity,
        Boolean isRequired,
        Integer displayOrder
) {
    public static CreateRewardOptionCommand from(RewardOptionCreateCommand command) {
        return new CreateRewardOptionCommand(
                command.name(),
                command.additionalPrice(),
                command.stockQuantity(),
                command.isRequired(),
                command.displayOrder()
        );
    }

    public static List<CreateRewardOptionCommand> from(List<RewardOptionCreateCommand> commandList) {
        if (commandList == null) {
            return null;
        }
        return commandList.stream()
                .map(CreateRewardOptionCommand::from)
                .toList();
    }
}