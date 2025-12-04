package com.nowayback.project.application.project.event;

import com.nowayback.project.application.event.EventHandler;
import com.nowayback.project.application.project.ProjectService;
import com.nowayback.project.application.project.event.payload.ProjectFundingFailedPayload;
import com.nowayback.project.domain.outbox.vo.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectFundingFailedEventHandler implements EventHandler<ProjectFundingFailedPayload> {

    private final ProjectService projectService;

    @Override
    public boolean supports(EventType eventType) {
        return EventType.PROJECT_FUNDING_FAILED == eventType;
    }

    @Override
    public Class<ProjectFundingFailedPayload> getPayloadType() {
        return ProjectFundingFailedPayload.class;
    }

    @Override
    public void handle(ProjectFundingFailedPayload payload) {
        projectService.projectEnded(payload.getProjectId(), false);
    }
}
