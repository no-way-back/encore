package com.nowayback.reward.presentation.reward.dto.request;

import com.nowayback.reward.domain.reward.vo.RewardType;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

public record UpdateRewardRequest(
        String name,
        String description,
        Integer price,
        Integer stockQuantity,
        Integer shippingFee,
        Integer freeShippingAmount,
        Integer purchaseLimitPerPerson,
        RewardType rewardType,
        List<UpdateRewardOptionRequest> options
) {
    @Getter
    public static class UpdateRewardOptionRequest {
        private UUID optionId;
        private String name;
        private Integer additionalPrice;
        private Integer stockQuantity;
        private Boolean isRequired;
        private Integer displayOrder;
    }
}
