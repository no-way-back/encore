package com.nowayback.project.presentation.projectdraft.dto.response;

import com.nowayback.project.application.dto.ProjectRewardDraftResult;
import java.util.List;
import java.util.UUID;

public record ProjectRewardDraftResponse(
    UUID projectDraftId,
    List<RewardDraftResponse> rewards
) {

    public static ProjectRewardDraftResponse from(ProjectRewardDraftResult result) {
        return new ProjectRewardDraftResponse(
            result.projectDraftId(),
            result.rewardDraftResults().stream()
                .map(RewardDraftResponse::from)
                .toList()
        );
    }

    public record RewardDraftResponse(
        String title,
        Long price,
        Integer shippingFee,
        Integer freeShippingAmount,
        Integer limitCount,
        Integer purchaseLimitPerPerson,
        List<RewardDraftOptionResponse> options
    ) {

        public static RewardDraftResponse from(ProjectRewardDraftResult.RewardDraftResult dto) {
            return new RewardDraftResponse(
                dto.title(),
                dto.price(),
                dto.shippingFee(),
                dto.freeShippingAmount(),
                dto.limitCount(),
                dto.purchaseLimitPerPerson(),
                dto.rewardDraftOptionResults().stream()
                    .map(RewardDraftOptionResponse::from)
                    .toList()
            );
        }
    }

    public record RewardDraftOptionResponse(
        Integer additionalPrice,
        Integer stockQuantity,
        Integer displayOrder
    ) {
        public static RewardDraftOptionResponse from(ProjectRewardDraftResult.RewardDraftOptionResult dto) {
            return new RewardDraftOptionResponse(
                dto.additionalPrice(),
                dto.stockQuantity(),
                dto.displayOrder()
            );
        }
    }
}
