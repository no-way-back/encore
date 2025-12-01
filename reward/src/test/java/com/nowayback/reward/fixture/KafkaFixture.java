package com.nowayback.reward.fixture;

import com.nowayback.reward.domain.reward.vo.RewardType;
import com.nowayback.reward.infrastructure.kafka.dto.project.data.RewardCreateData;
import com.nowayback.reward.infrastructure.kafka.dto.project.event.ProjectCreatedEvent;
import com.nowayback.reward.infrastructure.kafka.dto.project.payload.ProjectCreatedPayload;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class KafkaFixture {
    public static ProjectCreatedPayload createProjectCreatedPayload(UUID projectId, UUID creatorId, int rewardCount) {
        List<RewardCreateData> rewardData = java.util.stream.IntStream.range(0, rewardCount)
                .mapToObj(i -> new RewardCreateData(
                        "Reward" + i, "Desc", 25000, 100, 3000, 50000, 5, RewardType.GENERAL, null
                )).toList();

        return new ProjectCreatedPayload(projectId, creatorId, rewardData);
    }

    public static ProjectCreatedEvent createProjectCreatedEvent(String eventType, ProjectCreatedPayload payload) {
        return new ProjectCreatedEvent(
                UUID.randomUUID().toString(),
                eventType,
                LocalDateTime.now(),
                payload
        );
    }
}
