package com.nowayback.funding.presentation.queue.dto.request;

import com.nowayback.funding.application.queue.dto.command.EnqueueFundingCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

/**
 * 대기열 등록 요청
 */
public record EnqueueRequest(
        @NotNull(message = "프로젝트 ID는 필수입니다")
        UUID projectId,

        @NotNull(message = "금액은 필수입니다")
        @Positive(message = "금액은 0보다 커야 합니다")
        Integer amount,

        List<RewardItemRequest> rewardItems
) {

    public record RewardItemRequest(
            @NotNull UUID rewardId,
            UUID optionId,
            @NotNull @Positive Integer quantity
    ) {}

    public EnqueueFundingCommand toCommand(UUID userId, String idempotencyKey) {
        List<EnqueueFundingCommand.RewardItem> items = null;

        if (rewardItems != null) {
            items = rewardItems.stream()
                    .map(item -> new EnqueueFundingCommand.RewardItem(
                            item.rewardId(),
                            item.optionId(),
                            item.quantity()
                    ))
                    .toList();
        }

        return EnqueueFundingCommand.of(userId, projectId, amount, items, idempotencyKey);
    }
}