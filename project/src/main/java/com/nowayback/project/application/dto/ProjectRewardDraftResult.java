package com.nowayback.project.application.dto;

import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import com.nowayback.project.domain.projectDraft.entity.ProjectRewardDraft;
import com.nowayback.project.domain.projectDraft.entity.ProjectRewardOptionDraft;
import java.util.List;
import java.util.UUID;

public record ProjectRewardDraftResult(
    UUID projectDraftId,
    List<RewardDraftResult> rewardDraftResults
) {

    public static ProjectRewardDraftResult of(ProjectDraft projectDraft) {
        return new ProjectRewardDraftResult(
            projectDraft.getId(),
            projectDraft.getRewardDrafts().stream()
                .map(rewardDraft -> RewardDraftResult.of(rewardDraft))
                .toList()
        );
    }

    public record RewardDraftResult(
        String title,
        Long price,
        Integer shippingFee,
        Integer freeShippingAmount,
        Integer limitCount,
        Integer purchaseLimitPerPerson,
        List<RewardDraftOptionResult> rewardDraftOptionResults
    ) {

        public static RewardDraftResult of(ProjectRewardDraft rewardDraft) {
            return new RewardDraftResult(
                rewardDraft.getTitle(),
                rewardDraft.getRewardPrice().getPrice(),
                rewardDraft.getRewardPrice().getShippingFee(),
                rewardDraft.getRewardPrice().getFreeShippingAmount(),
                rewardDraft.getLimitCount(),
                rewardDraft.getPurchaseLimitPerPerson(),
                rewardDraft.getRewardOptions().asReadOnly().stream()
                    .map(rewardOption -> RewardDraftOptionResult.of(rewardOption))
                    .toList()
            );
        }
    }

    public record RewardDraftOptionResult(
        Integer additionalPrice,
        Integer stockQuantity,
        Integer displayOrder
        ) {
        public static RewardDraftOptionResult of(ProjectRewardOptionDraft rewardOptionDraft) {
            return new RewardDraftOptionResult(
                rewardOptionDraft.getAdditionalPrice(),
                rewardOptionDraft.getStockQuantity(),
                rewardOptionDraft.getDisplayOrder()
            );
        }
    }
}
