package com.nowayback.project.application.project;

import com.nowayback.project.application.project.command.CreateProjectCommand;
import com.nowayback.project.domain.project.entity.Project;
import com.nowayback.project.domain.project.repository.ProjectRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public UUID createProject(CreateProjectCommand command) {
        Project project = Project.create(
            command.userId(),
            command.title(),
            command.summary(),
            command.category(),
            command.thumbnailUrl(),
            command.contentHtml(),
            command.goalAmount(),
            command.fundingStartDate(),
            command.fundingEndDate()
        );

        projectRepository.save(project);
        return project.getId();
    }
}
