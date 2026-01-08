package com.nowayback.project.application.project;

import com.nowayback.project.application.project.command.CreateProjectCommand;
import com.nowayback.project.application.project.dto.CategoryCodes;
import com.nowayback.project.application.project.dto.Cursor;
import com.nowayback.project.application.project.dto.ProjectCardPageResult;
import com.nowayback.project.application.project.dto.ProjectListState;
import com.nowayback.project.application.project.dto.ProjectResult;
import com.nowayback.project.application.project.dto.SettlementResult;
import com.nowayback.project.domain.exception.ProjectErrorCode;
import com.nowayback.project.domain.exception.ProjectException;
import com.nowayback.project.domain.project.entity.Project;
import com.nowayback.project.domain.project.repository.ProjectRepository;
import com.nowayback.project.domain.project.vo.Period;
import com.nowayback.project.domain.project.vo.ProjectDraftId;
import com.nowayback.project.domain.project.vo.UserId;
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
            UserId.create(command.userId()),
            ProjectDraftId.create(command.projectDraftId()),
            command.title(),
            command.summary(),
            command.categoryId(),
            command.rootCategoryId(),
            command.thumbnailUrl(),
            command.contentHtml(),
            command.goalAmount(),
            Period.create(
                command.fundingStartDate(),
                command.fundingEndDate()
            ),
            command.account()
        );

        projectRepository.save(project);
        return project.getId();
    }

    public ProjectCardPageResult searchProjects(
        Cursor cursor,
        CategoryCodes categoryCodes,
        ProjectListState state
    ) {
        return ProjectCardPageResult.of(
            projectRepository.searchProjects(
                categoryCodes.rootCategoryCode(),
                categoryCodes.categoryCode(),
                ProjectListState.toProjectStatuses(state),
                cursor
            ),
            projectRepository.count(
                categoryCodes.rootCategoryCode(),
                categoryCodes.categoryCode(),
                ProjectListState.toProjectStatuses(state),
                PageLimitCalculator.calculatePageLimit(cursor.page(), cursor.size(), 10L)
            )
        );


    }

    public ProjectResult getProject(UUID projectId) {
        Project project = findProjectOrThrow(projectId);

        return ProjectResult.of(project);
    }

    public SettlementResult getProjectSettlement(UUID projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectException(ProjectErrorCode.FUNDING_DRAFT_NOT_FOUND));

        return SettlementResult.from(project);
    }

    @Transactional
    public void markAsUpcoming(UUID projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_NOT_FOUND));

        project.markAsUpcoming();
    }

    @Transactional
    public void markAsCreationFailed(UUID projectId, String reason) {
        Project project = findProjectOrThrow(projectId);

        project.markAsCreationFailed(reason);
    }

    @Transactional
    public void projectEnded(UUID projectId, boolean success) {
        Project project = findProjectOrThrow(projectId);

        project.endFunding(success);
    }

    private Project findProjectOrThrow(UUID projectId) {
        return projectRepository.findById(projectId)
            .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_NOT_FOUND));
    }
}
