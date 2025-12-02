package com.nowayback.project.application.outbox.event.payload;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectCreatedEventPayload implements EventPayload {
    private UUID projectId;

    public static ProjectCreatedEventPayload of(UUID projectId) {
        return new ProjectCreatedEventPayload(projectId);
    }
}
