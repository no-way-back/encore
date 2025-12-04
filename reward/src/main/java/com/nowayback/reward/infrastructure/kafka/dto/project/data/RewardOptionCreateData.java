package com.nowayback.reward.infrastructure.kafka.dto.project.data;

import com.nowayback.reward.application.reward.command.RewardOptionCreateCommand;

public record RewardOptionCreateData(
        String name,
        Long additionalPrice,
        Integer stockQuantity,
        Boolean isRequired,
        Integer displayOrder
) {
    public RewardOptionCreateCommand toCommand() {
        return new RewardOptionCreateCommand(
                this.name(),
                this.additionalPrice(),
                this.stockQuantity(),
                this.isRequired(),
                this.displayOrder()
        );
    }
}