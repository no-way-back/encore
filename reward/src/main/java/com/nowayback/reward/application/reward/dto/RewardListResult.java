package com.nowayback.reward.application.reward.dto;

import com.nowayback.reward.domain.reward.entity.RewardOptions;
import com.nowayback.reward.domain.reward.entity.Rewards;
import com.nowayback.reward.domain.reward.vo.SaleStatus;

import java.time.LocalDateTime;
import java.util.List;

public record RewardListResult(
        List<RewardItem> rewards
) {
    public static RewardListResult from(List<Rewards> rewardList) {
        List<RewardItem> items = rewardList.stream()
                .map(RewardItem::from)
                .toList();

        return new RewardListResult(items);
    }

    public record RewardItem(
            String rewardId,
            String name,
            String description,
            Long price,
            Integer stockQuantity,
            Integer shippingFee,
            Integer freeShippingAmount,
            Integer purchaseLimitPerPerson,
            SaleStatus status,
            List<OptionItem> optionList,
            LocalDateTime createdAt
    ) {
        public static RewardItem from(Rewards reward) {
            List<OptionItem> options = reward.getOptionList().stream()
                    .map(OptionItem::from)
                    .toList();

            return new RewardItem(
                    reward.getId().toString(),
                    reward.getName(),
                    reward.getDescription(),
                    reward.getPrice().getAmount(),
                    reward.getTotalAvailableStock(),
                    reward.getShippingPolicy().getShippingFee(),
                    reward.getShippingPolicy().getFreeShippingAmount(),
                    reward.getPurchaseLimitPerPerson(),
                    reward.getStatus(),
                    options,
                    reward.getCreatedAt()
            );
        }
    }

    public record OptionItem(
            String optionId,
            String name,
            Long additionalPrice
    ) {
        public static OptionItem from(RewardOptions option) {
            return new OptionItem(
                    option.getId().toString(),
                    option.getName(),
                    option.getAdditionalPrice().getAmount()
            );
        }
    }
}