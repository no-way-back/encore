package com.nowayback.project.application.projectdraft.event.payload;

import com.nowayback.project.application.event.EventPayload;
import com.nowayback.project.domain.projectDraft.entity.ProjectRewardDraft;
import com.nowayback.project.domain.projectDraft.entity.ProjectRewardOptionDraft;
import com.nowayback.project.domain.projectDraft.vo.RewardPrice;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class RewardCreationEventPayload implements EventPayload {

    private UUID projectId;
    private List<RewardItemPayload> rewards;

    private RewardCreationEventPayload(UUID projectId, List<RewardItemPayload> rewards) {
        this.projectId = projectId;
        this.rewards = rewards;
    }

    public static RewardCreationEventPayload from(
        UUID projectId,
        List<ProjectRewardDraft> drafts
    ) {
        List<RewardItemPayload> items = drafts.stream()
            .map(RewardItemPayload::from)
            .toList();

        return new RewardCreationEventPayload(projectId, items);
    }

    @Getter
    @ToString
    @NoArgsConstructor
    public static class RewardItemPayload {

        private String title;
        private Long price;
        private Integer shippingFee;
        private Integer freeShippingAmount;
        private Integer limitCount;
        private Integer purchaseLimitPerPerson;
        private List<RewardOptionPayload> options;

        private RewardItemPayload(
            String title,
            Long price,
            Integer shippingFee,
            Integer freeShippingAmount,
            Integer limitCount,
            Integer purchaseLimitPerPerson,
            List<RewardOptionPayload> options
        ) {
            this.title = title;
            this.price = price;
            this.shippingFee = shippingFee;
            this.freeShippingAmount = freeShippingAmount;
            this.limitCount = limitCount;
            this.purchaseLimitPerPerson = purchaseLimitPerPerson;
            this.options = options;
        }

        public static RewardItemPayload from(ProjectRewardDraft draft) {

            RewardPrice rewardPrice = draft.getRewardPrice();

            List<RewardOptionPayload> optionPayloads = draft.getRewardOptions().asReadOnly()
                .stream()
                .map(RewardOptionPayload::from)
                .toList();

            return new RewardItemPayload(
                draft.getTitle(),
                rewardPrice.getPrice(),
                rewardPrice.getShippingFee(),
                rewardPrice.getFreeShippingAmount(),
                draft.getLimitCount(),
                draft.getPurchaseLimitPerPerson(),
                optionPayloads
            );
        }
    }

    @Getter
    @ToString
    @NoArgsConstructor
    public static class RewardOptionPayload {

        private Integer additionalPrice;
        private Integer stockQuantity;
        private Integer displayOrder;

        private RewardOptionPayload(
            Integer additionalPrice,
            Integer stockQuantity,
            Integer displayOrder
        ) {
            this.additionalPrice = additionalPrice;
            this.stockQuantity = stockQuantity;
            this.displayOrder = displayOrder;
        }

        public static RewardOptionPayload from(ProjectRewardOptionDraft option) {
            return new RewardOptionPayload(
                option.getAdditionalPrice(),
                option.getStockQuantity(),
                option.getDisplayOrder()
            );
        }
    }
}
