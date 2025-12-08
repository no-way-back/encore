package com.nowayback.project.infrastructure.project.persistence;

import com.nowayback.project.domain.project.entity.ProjectStatusHistory;
import com.nowayback.project.domain.project.repository.ProjectStatusHistoryRepository;
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
}
