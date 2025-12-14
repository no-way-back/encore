package com.nowayback.reward.infrastructure.kafka.dto.project.event;

import com.nowayback.reward.application.reward.dto.RewardCreationResult;
import com.nowayback.reward.domain.outbox.vo.EventType;
import com.nowayback.reward.infrastructure.kafka.dto.project.payload.RewardCreationResultPayload;

import java.time.LocalDateTime;
import java.util.UUID;

public record RewardCreationResultEvent(
        String eventId,
        EventType eventType,
        LocalDateTime occurredAt,
        RewardCreationResultPayload payload
) {
    public static RewardCreationResultEvent from(RewardCreationResult result) {
        EventType eventType = result.success()
                ? EventType.REWARD_CREATION_SUCCESS
                : EventType.REWARD_CREATION_FAILED;

        return new RewardCreationResultEvent(
                UUID.randomUUID().toString(),
                eventType,
                LocalDateTime.now(),
                new RewardCreationResultPayload(
                        result.projectId().toString(),
                        result.success()
                )
        );
    }
}