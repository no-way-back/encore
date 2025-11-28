package com.nowayback.project.infrastructure.projectdraft;

import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import com.nowayback.project.domain.projectDraft.repository.ProjectDraftRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectDraftRepositoryImpl implements ProjectDraftRepository {

    private final ProjectDraftJpaRepository projectDraftJpaRepository;

    @Override
    public ProjectDraft save(ProjectDraft projectDraft) {
        return projectDraftJpaRepository.save(projectDraft);
    }

    @Override
    public Optional<ProjectDraft> findById(UUID projectDraftId) {
        return projectDraftJpaRepository.findById(projectDraftId);
    }
}
