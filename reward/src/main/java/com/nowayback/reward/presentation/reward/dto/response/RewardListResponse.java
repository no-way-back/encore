package com.nowayback.reward.presentation.reward.dto.response;

import com.nowayback.reward.application.reward.dto.RewardListResult;
import com.nowayback.reward.domain.reward.vo.SaleStatus;

import java.time.LocalDateTime;
import java.util.List;

public record RewardListResponse(
        List<RewardResponse> rewards
) {
    public static RewardListResponse from(RewardListResult result) {
        List<RewardResponse> responses = result.rewards().stream()
                .map(RewardResponse::from)
                .toList();

        return new RewardListResponse(responses);
    }

    public record RewardResponse(
            String rewardId,
            String name,
            String description,
            Long price,
            Integer stockQuantity,
            Integer shippingFee,
            Integer freeShippingAmount,
            Integer purchaseLimitPerPerson,
            SaleStatus status,
            List<OptionResponse> optionList,
            LocalDateTime createdAt
    ) {
        public static RewardResponse from(RewardListResult.RewardItem item) {
            List<OptionResponse> options = item.optionList().stream()
                    .map(OptionResponse::from)
                    .toList();

            return new RewardResponse(
                    item.rewardId(),
                    item.name(),
                    item.description(),
                    item.price(),
                    item.stockQuantity(),
                    item.shippingFee(),
                    item.freeShippingAmount(),
                    item.purchaseLimitPerPerson(),
                    item.status(),
                    options,
                    item.createdAt()
            );
        }
    }

    public record OptionResponse(
            String optionId,
            String name,
            Long additionalPrice
    ) {
        public static OptionResponse from(RewardListResult.OptionItem item) {
            return new OptionResponse(
                    item.optionId(),
                    item.name(),
                    item.additionalPrice()
            );
        }
    }
}