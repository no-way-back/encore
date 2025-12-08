package com.nowayback.reward.infrastructure.kafka.dto.project.event;

import com.nowayback.reward.infrastructure.kafka.constant.EventType;
import com.nowayback.reward.infrastructure.kafka.dto.project.payload.RewardCreationResultPayload;

import java.time.LocalDateTime;

import static com.nowayback.reward.infrastructure.kafka.constant.EventType.REWARD_CREATION_FAILED;
import static com.nowayback.reward.infrastructure.kafka.constant.EventType.REWARD_CREATION_SUCCESS;

public record RewardCreationResultEvent(
        String eventId,
        EventType eventType,
        LocalDateTime occurredAt,
        RewardCreationResultPayload payload
) {

    /**
     * 리워드 생성 성공
     * - event type = REWARD_CREATION_SUCCESS
     * - success = true
     */
    public static RewardCreationResultEvent success(String projectId) {
        return new RewardCreationResultEvent(
                java.util.UUID.randomUUID().toString(),
                REWARD_CREATION_SUCCESS,
                LocalDateTime.now(),
                new RewardCreationResultPayload(projectId, true)
        );
    }

    /**
     * 리워드 생성 실패
     * - event type = REWARD_CREATION_FAILED
     * - success = false
     */
    public static RewardCreationResultEvent failure(String projectId) {
        return new RewardCreationResultEvent(
                java.util.UUID.randomUUID().toString(),
                REWARD_CREATION_FAILED,
                LocalDateTime.now(),
                new RewardCreationResultPayload(projectId, false)
        );
    }
}