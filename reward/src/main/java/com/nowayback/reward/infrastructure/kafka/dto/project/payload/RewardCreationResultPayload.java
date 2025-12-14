package com.nowayback.reward.infrastructure.kafka.dto.project.payload;

public record RewardCreationResultPayload(
        String projectId,
        boolean success
) {}