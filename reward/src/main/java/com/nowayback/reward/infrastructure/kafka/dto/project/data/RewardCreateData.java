package com.nowayback.reward.infrastructure.kafka.dto.project.data;

import com.nowayback.reward.application.reward.command.RewardCreateCommand;
import com.nowayback.reward.application.reward.command.RewardOptionCreateCommand;
import com.nowayback.reward.domain.reward.vo.RewardType;

import java.util.List;

public record RewardCreateData(
        String name,
        String description,
        Long price,
        Integer stockQuantity,
        Integer shippingFee,
        Integer freeShippingAmount,
        Integer purchaseLimitPerPerson,
        RewardType rewardType,
        List<RewardOptionCreateData> options
) {
    public RewardCreateCommand toCommand() {
        List<RewardOptionCreateCommand> optionCommands = toOptionCommands(this.options());

        return new RewardCreateCommand(
                this.name(),
                this.description(),
                this.price(),
                this.stockQuantity(),
                this.shippingFee(),
                this.freeShippingAmount(),
                this.purchaseLimitPerPerson(),
                this.rewardType(),
                optionCommands
        );
    }

    private List<RewardOptionCreateCommand> toOptionCommands(List<RewardOptionCreateData> optionList) {
        if (optionList == null) {
            return List.of();
        }
        return optionList.stream()
                .map(RewardOptionCreateData::toCommand)
                .toList();
    }

    public static List<RewardCreateCommand> toCommands(List<RewardCreateData> requests) {
        return requests.stream()
                .map(RewardCreateData::toCommand)
                .toList();
    }
}