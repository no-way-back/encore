package com.nowayback.project.application.project.event;

import com.nowayback.project.application.event.EventHandler;
import com.nowayback.project.application.project.ProjectService;
import com.nowayback.project.application.project.event.payload.ProjectFundingSuccessPayload;
import com.nowayback.project.domain.outbox.vo.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectFundingSuccessEventHandler implements EventHandler<ProjectFundingSuccessPayload> {

    private final ProjectService projectService;

    @Override
    public boolean supports(EventType eventType) {
        return EventType.PROJECT_FUNDING_SUCCEEDED == eventType;
    }

    @Override
    public Class<ProjectFundingSuccessPayload> getPayloadType() {
        return ProjectFundingSuccessPayload.class;
    }

    @Override
    public void handle(ProjectFundingSuccessPayload payload) {
        log.info("[ProjectFundingSuccessEventHandler.handle] {}", payload);
        projectService.projectEnded(payload.getProjectId(), true);
    }
}
