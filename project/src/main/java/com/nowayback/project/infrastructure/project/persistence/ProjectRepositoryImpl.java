package com.nowayback.project.infrastructure.project.persistence;

import com.nowayback.project.application.project.dto.ProjectCard;
import com.nowayback.project.domain.project.entity.Project;
import com.nowayback.project.domain.project.repository.ProjectRepository;
import com.nowayback.project.domain.project.vo.ProjectSortType;
import com.nowayback.project.domain.project.vo.ProjectStatus;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<ProjectCard> searchProjects(
        String rootCategoryCode,
        String categoryCode,
        Set<ProjectStatus> statuses,
        ProjectSortType sortType,
        Pageable pageable
    ) {
        return projectQueryRepository.searchProjects(
            rootCategoryCode,
            categoryCode,
            statuses,
            sortType,
            pageable
        );
    }
}
