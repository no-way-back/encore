package com.nowayback.project.domain.project.repository;

import com.nowayback.project.application.project.dto.ProjectCard;
import com.nowayback.project.domain.project.entity.Project;
import com.nowayback.project.domain.project.vo.ProjectSortType;
import com.nowayback.project.domain.project.vo.ProjectStatus;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(UUID projectId);

    Page<ProjectCard> searchProjects(
        String rootCategoryCode,
        String categoryCode,
        Set<ProjectStatus> statuses,
        ProjectSortType sortType,
        Pageable pageable
    );
}
