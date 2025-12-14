package com.nowayback.project.application.projectdraft.event.payload;

import com.nowayback.project.domain.outbox.vo.EventPayload;
import com.nowayback.project.domain.projectDraft.entity.ProjectRewardDraft;
import com.nowayback.project.domain.projectDraft.entity.ProjectRewardOptionDraft;
import com.nowayback.project.domain.projectDraft.vo.RewardPrice;
import com.nowayback.project.domain.projectDraft.vo.RewardType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class RewardCreationEventPayload implements EventPayload {

    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private Payload payload;

    private RewardCreationEventPayload(
        UUID projectId,
        UUID creatorId,
        List<RewardItemPayload> rewards
    ) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = "PROJECT_CREATED";
        this.timestamp = LocalDateTime.now();
        this.payload = Payload.from(projectId, creatorId, rewards);
    }

    public static RewardCreationEventPayload from(
        UUID projectId,
        UUID creatorId,
        List<ProjectRewardDraft> drafts
    ) {
        List<RewardItemPayload> items = drafts.stream()
            .map(RewardItemPayload::from)
            .toList();

        return new RewardCreationEventPayload(projectId, creatorId, items);
    }

    @Getter
    @ToString
    @NoArgsConstructor
    public static class Payload {
        private UUID projectId;
        private UUID creatorId;
        private List<RewardItemPayload> rewards;

        private Payload(UUID projectId, UUID creatorId, List<RewardItemPayload> rewards) {
            this.projectId = projectId;
            this.creatorId = creatorId;
            this.rewards = rewards;
        }

        public static Payload from(UUID projectId, UUID creatorId, List<RewardItemPayload> rewards) {
            return new Payload(projectId, creatorId, rewards);
        }
    }

    @Getter
    @ToString
    @NoArgsConstructor
    public static class RewardItemPayload {

        private String name;
        private RewardType rewardType;
        private Long price;
        private Integer shippingFee;
        private Integer freeShippingAmount;
        private Integer stockQuantity;
        private Integer purchaseLimitPerPerson;
        private List<RewardOptionPayload> options;

        private RewardItemPayload(
            String name,
            RewardType rewardType,
            Long price,
            Integer shippingFee,
            Integer freeShippingAmount,
            Integer stockQuantity,
            Integer purchaseLimitPerPerson,
            List<RewardOptionPayload> options
        ) {
            this.name = name;
            this.rewardType = rewardType;
            this.price = price;
            this.shippingFee = shippingFee;
            this.freeShippingAmount = freeShippingAmount;
            this.stockQuantity = stockQuantity;
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
                draft.getRewardType(),
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

        private String name;
        private Boolean isRequired;
        private Integer additionalPrice;
        private Integer stockQuantity;
        private Integer displayOrder;

        private RewardOptionPayload(
            String name,
            Boolean isRequired,
            Integer additionalPrice,
            Integer stockQuantity,
            Integer displayOrder
        ) {
            this.name = name;
            this.isRequired = isRequired;
            this.additionalPrice = additionalPrice;
            this.stockQuantity = stockQuantity;
            this.displayOrder = displayOrder;
        }

        public static RewardOptionPayload from(ProjectRewardOptionDraft option) {
            return new RewardOptionPayload(
                option.getName(),
                option.getIsRequired(),
                option.getAdditionalPrice(),
                option.getStockQuantity(),
                option.getDisplayOrder()
            );
        }
    }
}
