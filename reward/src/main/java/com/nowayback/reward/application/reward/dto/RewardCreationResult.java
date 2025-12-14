package com.nowayback.reward.application.reward.dto;

import java.util.UUID;

public record RewardCreationResult(
        UUID projectId,
        boolean success
) {
    public static RewardCreationResult success(UUID projectId) {
        return new RewardCreationResult(projectId, true);
    }

    public static RewardCreationResult failure(UUID projectId) {
        return new RewardCreationResult(projectId, false);
    }
}