package com.nowayback.reward.application.reward.command;

public record RewardOptionCreateCommand(
        String name,
        Integer additionalPrice,
        Integer stockQuantity,
        Boolean isRequired,
        Integer displayOrder
) {
}
