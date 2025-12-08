package com.nowayback.project.domain.project.repository;

import com.nowayback.project.domain.project.entity.ProjectStatusHistory;
import java.util.Optional;
import java.util.UUID;

public interface ProjectStatusHistoryRepository {
    ProjectStatusHistory save(ProjectStatusHistory projectStatusHistory);

    Optional<ProjectStatusHistory> findByProjectId(UUID id);
}
