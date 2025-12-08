package com.nowayback.project.domain.project.event;

import com.nowayback.project.domain.outbox.vo.AggregateType;
import com.nowayback.project.domain.outbox.vo.EventType;
import com.nowayback.project.domain.project.vo.ProjectStatus;
import com.nowayback.project.domain.shard.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectStatusUpdatedEvent implements DomainEvent {

    private final UUID projectId;
    private final ProjectStatus fromStatus;
    private final ProjectStatus toStatus;
    private final LocalDateTime occurredAt;

    public static ProjectStatusUpdatedEvent create(
        UUID projectId,
        ProjectStatus fromStatus,
        ProjectStatus toStatus
    ) {
        return new ProjectStatusUpdatedEvent(
            projectId,
            fromStatus,
            toStatus,
            LocalDateTime.now()
        );
    }

    @Override
    public UUID getAggregateId() {
        return projectId;
    }

    @Override
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public AggregateType getAggregateType() {
        return AggregateType.PROJECT;
    }

    @Override
    public EventType getEventType() {
        return EventType.PROJECT_STATUS_UPDATED;
    }
}
