package com.nowayback.project.domain.project.repository;

import com.nowayback.project.domain.project.entity.ProjectStatusHistory;

public interface ProjectStatusHistoryRepository {
    ProjectStatusHistory save(ProjectStatusHistory projectStatusHistory);
}
