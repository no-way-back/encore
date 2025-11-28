package com.nowayback.project.application.command;

import java.util.List;
import java.util.UUID;

public record SaveRewardDraftCommand(
    UUID projectDraftId,
    List<RewardDraftCommand> saveRewardCommands
) {

    public static SaveRewardDraftCommand of(
        UUID projectDraftId,
        List<RewardDraftCommand> saveRewardCommands
    ) {
        return new SaveRewardDraftCommand(projectDraftId, saveRewardCommands);
    }

    public record RewardDraftCommand(
        String title,
        Long price,
        Integer limitCount,
        Integer shippingFee,
        Integer freeShippingAmount,
        Integer purchaseLimitPerPerson,
        List<RewardOptionCommand> rewardOptionCommands
    ) {

        public static RewardDraftCommand of(
            String title,
            Long price,
            Integer limitCount,
            Integer shippingFee,
            Integer freeShippingAmount,
            Integer purchaseLimitPerPerson,
            List<RewardOptionCommand> rewardOptionCommands
        ) {
            return new RewardDraftCommand(
                title,
                price,
                limitCount,
                shippingFee,
                freeShippingAmount,
                purchaseLimitPerPerson,
                rewardOptionCommands
            );
        }
    }

    public record RewardOptionCommand(
        Integer additionalPrice,
        Integer stockQuantity,
        Integer displayOrder
    ) {

        public static RewardOptionCommand of(
            Integer additionalPrice,
            Integer stockQuantity,
            Integer displayOrder
        ) {
            return new RewardOptionCommand(additionalPrice, stockQuantity, displayOrder);
        }
    }
}
