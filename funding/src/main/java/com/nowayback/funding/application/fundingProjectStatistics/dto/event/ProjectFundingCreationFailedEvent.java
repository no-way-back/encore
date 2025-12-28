package com.nowayback.funding.application.fundingProjectStatistics.dto.event;

import com.nowayback.funding.application.fundingProjectStatistics.dto.event.payload.ProjectFundingCreationFailedPayload;
import com.nowayback.funding.domain.event.EventType;
import com.nowayback.funding.domain.event.OutboxEventMetadata;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectFundingCreationFailedEvent(
        UUID eventId,
        EventType eventType,
        LocalDateTime timestamp,
        ProjectFundingCreationFailedPayload payload
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

    public static ProjectFundingCreationFailedEvent of(
            UUID projectId,
            UUID creatorId,
            Long targetAmount,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String failureReason
    ) {
        return new ProjectFundingCreationFailedEvent(
                UUID.randomUUID(),
                EventType.PROJECT_FUNDING_CREATED_FAILED,
                LocalDateTime.now(),
                new ProjectFundingCreationFailedPayload(
                        projectId,
                        creatorId,
                        targetAmount,
                        startDate,
                        endDate,
                        failureReason
                )
        );
    }
}