package com.nowayback.project.application.project;

import com.nowayback.project.application.project.command.SaveProjectStatusHistoryCommand;
import com.nowayback.project.domain.project.entity.ProjectStatusHistory;
import com.nowayback.project.domain.project.repository.ProjectStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectStatusHistoryService {
    private final ProjectStatusHistoryRepository projectStatusHistoryRepository;

    @Transactional
    public ProjectStatusHistory save(SaveProjectStatusHistoryCommand command) {
        return projectStatusHistoryRepository.save(ProjectStatusHistory.create(
            command.projectId(),
            command.fromStatus(),
            command.toStatus(),
            command.occurredAt()
        ));
    }
}
