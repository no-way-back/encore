package com.nowayback.reward.application.reward.command;

import java.util.UUID;

import static com.nowayback.reward.presentation.reward.dto.request.UpdateRewardRequest.*;

public record UpdateRewardOptionCommand(
        UUID optionId,
        String name,
        Long additionalPrice,
        Integer stockQuantity,
        Boolean isRequired,
        Integer displayOrder
) {
    public static UpdateRewardOptionCommand from(UpdateRewardOptionRequest request) {
        return new UpdateRewardOptionCommand(
                request.getOptionId(),
                request.getName(),
                request.getAdditionalPrice(),
                request.getStockQuantity(),
                request.getIsRequired(),
                request.getDisplayOrder()
        );
    }
}