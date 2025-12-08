package com.nowayback.project.application.project.command;

import com.nowayback.project.domain.project.event.ProjectStatusUpdatedEvent;
import com.nowayback.project.domain.project.vo.ProjectStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record SaveProjectStatusHistoryCommand(
    UUID projectId,
    ProjectStatus fromStatus,
    ProjectStatus toStatus,
    LocalDateTime occurredAt
) {

    public static SaveProjectStatusHistoryCommand from(ProjectStatusUpdatedEvent projectStatusUpdatedEvent) {
        return new SaveProjectStatusHistoryCommand(
            projectStatusUpdatedEvent.getProjectId(),
            projectStatusUpdatedEvent.getFromStatus(),
            projectStatusUpdatedEvent.getToStatus(),
            projectStatusUpdatedEvent.getOccurredAt()
        );
    }
}
