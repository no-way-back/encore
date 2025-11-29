package com.nowayback.project.presentation.projectdraft.dto.request;

import com.nowayback.project.application.projectdraft.command.SaveRewardDraftCommand;
import java.util.List;
import java.util.UUID;

public record SaveRewardDraftRequest(
    List<RewardDraftRequest> rewards
) {

    public SaveRewardDraftCommand toCommand(UUID draftId, UUID userId) {

        List<SaveRewardDraftCommand.RewardDraftCommand> rewardCommands =
            rewards.stream()
                .map(reward ->
                    SaveRewardDraftCommand.RewardDraftCommand.of(
                        reward.title(),
                        reward.price(),
                        reward.limitCount(),
                        reward.shippingFee(),
                        reward.freeShippingAmount(),
                        reward.purchaseLimitPerPerson(),
                        reward.options() == null
                            ? List.of()
                            : reward.options().stream()
                                .map(o ->
                                    SaveRewardDraftCommand.RewardOptionCommand.of(
                                        o.additionalPrice(),
                                        o.stockQuantity(),
                                        o.displayOrder()
                                    )
                                )
                                .toList()
                    )
                )
                .toList();

        return SaveRewardDraftCommand.of(draftId, rewardCommands);
    }

    public record RewardDraftRequest(
        String title,
        Long price,
        Integer limitCount,
        Integer shippingFee,
        Integer freeShippingAmount,
        Integer purchaseLimitPerPerson,
        List<RewardOptionRequest> options
    ) {}

    public record RewardOptionRequest(
        Integer additionalPrice,
        Integer stockQuantity,
        Integer displayOrder
    ) {}
}
