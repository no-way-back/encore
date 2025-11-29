package com.nowayback.reward.infrastructure.kafka.dto.project.request;

public record RewardOptionCreateRequest(
        String name,
        Integer additionalPrice,
        Integer stockQuantity,
        Boolean isRequired,
        Integer displayOrder
) {
}