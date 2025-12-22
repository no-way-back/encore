package com.nowayback.reward.infrastructure.kafka.dto.project.event;

import com.nowayback.reward.domain.vo.EventType;
import com.nowayback.reward.infrastructure.kafka.dto.project.payload.ProjectCreatedPayload;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectCreatedEvent(
        UUID eventId,
        EventType eventType,
        LocalDateTime timestamp,
        ProjectCreatedPayload payload
) {
}