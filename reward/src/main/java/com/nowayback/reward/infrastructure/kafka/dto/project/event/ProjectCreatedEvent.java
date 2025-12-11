package com.nowayback.reward.infrastructure.kafka.dto.project.event;

import com.nowayback.reward.domain.outbox.vo.EventType;
import com.nowayback.reward.infrastructure.kafka.dto.project.payload.ProjectCreatedPayload;

import java.time.LocalDateTime;

public record ProjectCreatedEvent(
        String eventId,
        EventType eventType,
        LocalDateTime timestamp,
        ProjectCreatedPayload payload
) {
}