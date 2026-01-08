package com.nowayback.project.domain.project.repository;

import com.nowayback.project.application.project.dto.Cursor;
import com.nowayback.project.application.project.dto.ProjectCard;
import com.nowayback.project.domain.project.entity.Project;
import com.nowayback.project.domain.project.vo.ProjectStatus;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(UUID projectId);

    List<ProjectCard> searchProjects(
        String rootCategoryCode,
        String categoryCode,
        Set<ProjectStatus> statuses,
        Cursor cursor
    );

    Long count(
        String rootCategoryCode,
        String categoryCode,
        Set<ProjectStatus> statuses,
        Long limit
    );
}
