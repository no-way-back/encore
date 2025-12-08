package com.nowayback.project.infrastructure.project.persistence;

import com.nowayback.project.domain.project.entity.ProjectStatusHistory;
import com.nowayback.project.domain.project.repository.ProjectStatusHistoryRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectStatusHistoryRepositoryImpl implements ProjectStatusHistoryRepository {

    private final ProjectStatusHistoryJpaRepository projectStatusHistoryJpaRepository;
    @Override
    public ProjectStatusHistory save(ProjectStatusHistory projectStatusHistory) {
        return projectStatusHistoryJpaRepository.save(projectStatusHistory);
    }

    @Override
    public Optional<ProjectStatusHistory> findByProjectId(UUID projectId) {
        return projectStatusHistoryJpaRepository.findByProjectId(projectId);
    }
}
