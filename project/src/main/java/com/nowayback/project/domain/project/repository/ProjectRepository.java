package com.nowayback.project.domain.project.repository;

import com.nowayback.project.domain.project.entity.Project;
import com.nowayback.project.domain.project.vo.ProjectStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(UUID projectId);

    Page<Project> searchProjects(ProjectStatus status, int page, int size);
}
