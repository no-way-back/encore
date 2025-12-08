package com.nowayback.project.application.project.event;

import com.nowayback.project.application.event.AsyncTransactionalEventListener;
import com.nowayback.project.application.project.ProjectStatusHistoryService;
import com.nowayback.project.application.project.command.SaveProjectStatusHistoryCommand;
import com.nowayback.project.domain.project.event.ProjectStatusUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectStatusUpdatedEventListener extends AsyncTransactionalEventListener<ProjectStatusUpdatedEvent> {

    private final ProjectStatusHistoryService projectStatusHistoryService;
    @Override
    protected void processEvent(ProjectStatusUpdatedEvent event) {
        projectStatusHistoryService.save(SaveProjectStatusHistoryCommand.from(event));
    }
}
