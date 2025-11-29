package com.nowayback.reward.infrastructure.kafka.dto.project.payload;

import com.nowayback.reward.infrastructure.kafka.dto.project.data.RewardCreateData;

import java.util.List;
import java.util.UUID;

public record ProjectCreatedPayload(
        UUID projectId,
        UUID creatorId,
        List<RewardCreateData> rewards
) {
}