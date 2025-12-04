package com.nowayback.reward.application.reward.command;

public record RewardOptionCreateCommand(
        String name,
        Long additionalPrice,
        Integer stockQuantity,
        Boolean isRequired,
        Integer displayOrder
) {
}
