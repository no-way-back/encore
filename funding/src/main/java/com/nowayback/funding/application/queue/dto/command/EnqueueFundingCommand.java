package com.nowayback.funding.application.queue.dto.command;

import java.util.List;
import java.util.UUID;

/**
 * 대기열 등록 Command
 */
public record EnqueueFundingCommand(
        UUID userId,
        UUID projectId,
        Integer amount,
        List<RewardItem> rewardItems,
        String idempotencyKey
) {

    public record RewardItem(
            UUID rewardId,
            UUID optionId,
            Integer quantity
    ) {}

    public static EnqueueFundingCommand of(
            UUID userId,
            UUID projectId,
            Integer amount,
            List<RewardItem> rewardItems,
            String idempotencyKey
    ) {
        return new EnqueueFundingCommand(userId, projectId, amount, rewardItems, idempotencyKey);
    }
}