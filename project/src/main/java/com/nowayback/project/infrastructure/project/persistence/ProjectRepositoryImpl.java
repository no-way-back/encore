package com.nowayback.project.infrastructure.project.persistence;

import com.nowayback.project.application.project.dto.Cursor;
import com.nowayback.project.application.project.dto.ProjectCard;
import com.nowayback.project.domain.project.entity.Project;
import com.nowayback.project.domain.project.repository.ProjectRepository;
import com.nowayback.project.domain.project.vo.ProjectStatus;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {

    private final ProjectJpaRepository projectJpaRepository;

    private final ProjectQueryRepository projectQueryRepository;

    @Override
    public Project save(Project project) {
        return projectJpaRepository.save(project);
    }

    @Override
    public Optional<Project> findById(UUID projectId) {
        return projectJpaRepository.findById(projectId);
    }

    @Override
    public List<ProjectCard> searchProjects(
        String rootCategoryCode,
        String categoryCode,
        Set<ProjectStatus> statuses,
        Cursor cursor
    ) {
        return projectQueryRepository.searchProjects(
            rootCategoryCode,
            categoryCode,
            statuses,
            cursor
        );
    }

    @Override
    public Long count(
        String rootCategoryCode,
        String categoryCode,
        Set<ProjectStatus> statuses,
        Long limit
    ) {
        return projectQueryRepository.count(rootCategoryCode, categoryCode, statuses, limit);
    }

}
