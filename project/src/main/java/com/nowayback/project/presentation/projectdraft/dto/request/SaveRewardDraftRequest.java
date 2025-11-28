package com.nowayback.project.presentation.projectdraft.dto.request;

import java.util.List;

public record SaveRewardDraftRequest(
    List<RewardDraftRequest> rewards
) {

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
