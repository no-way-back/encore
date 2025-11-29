package com.nowayback.reward.domain.reward.handler.command;

public record RewardOptionCreateCommand(
        String name,
        Integer additionalPrice,
        Integer stockQuantity,
        Boolean isRequired,
        Integer displayOrder
) {
}
