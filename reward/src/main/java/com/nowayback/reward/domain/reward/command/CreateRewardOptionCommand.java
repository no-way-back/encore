package com.nowayback.reward.domain.reward.command;

import com.nowayback.reward.infrastructure.kafka.dto.project.request.RewardOptionCreateRequest;

import java.util.List;

public record CreateRewardOptionCommand(
        String name,
        Integer additionalPrice,
        Integer stockQuantity,
        Boolean isRequired,
        Integer displayOrder
) {
    public static CreateRewardOptionCommand from(RewardOptionCreateRequest request) {
        return new CreateRewardOptionCommand(
                request.name(),
                request.additionalPrice(),
                request.stockQuantity(),
                request.isRequired(),
                request.displayOrder()
        );
    }

    public static List<CreateRewardOptionCommand> from(List<RewardOptionCreateRequest> requests) {
        if (requests == null) {
            return null;
        }
        return requests.stream()
                .map(CreateRewardOptionCommand::from)
                .toList();
    }
}