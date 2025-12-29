package com.nowayback.funding.application.fundingProjectStatistics.dto.event;

import com.nowayback.funding.application.fundingProjectStatistics.dto.event.payload.ProjectFundingFailedPayload;
import com.nowayback.funding.domain.event.EventType;
import com.nowayback.funding.domain.event.OutboxEventMetadata;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectFundingFailedEvent(
        UUID eventId,
        EventType eventType,
        LocalDateTime timestamp,
        ProjectFundingFailedPayload payload
) implements OutboxEventMetadata {

    @Override
    public String getAggregateType() {
        return "FUNDING_PROJECT";
    }

    @Override
    public UUID getAggregateId() {
        return payload.projectId();
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    public static ProjectFundingFailedEvent of(
            UUID projectId,
            Long finalAmount,
            Integer participantCount,
            Long targetAmount,
            Double achievementRate
    ) {
        return new ProjectFundingFailedEvent(
                UUID.randomUUID(),
                EventType.PROJECT_FUNDING_FAILED,
                LocalDateTime.now(),
                new ProjectFundingFailedPayload(
                        projectId,
                        finalAmount,
                        participantCount,
                        targetAmount,
                        achievementRate
                )
        );
    }
}