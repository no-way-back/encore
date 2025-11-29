package com.nowayback.project.infrastructure.projectdraft;

import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import com.nowayback.project.domain.projectDraft.repository.ProjectDraftRepository;
import com.nowayback.project.domain.projectDraft.vo.ProjectDraftStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectDraftRepositoryImpl implements ProjectDraftRepository {

    private final ProjectDraftJpaRepository projectDraftJpaRepository;

    private final ProjectDraftQueryRepository projectDraftQueryRepository;

    @Override
    public ProjectDraft save(ProjectDraft projectDraft) {
        return projectDraftJpaRepository.save(projectDraft);
    }

    @Override
    public Optional<ProjectDraft> findById(UUID projectDraftId) {
        return projectDraftJpaRepository.findById(projectDraftId);
    }

    @Override
    public List<ProjectDraft> findByUserId(UUID userId) {
        return projectDraftJpaRepository.findByUserId(userId);
    }

    @Override
    public Page<ProjectDraft> searchDrafts(
        UUID userId,
        ProjectDraftStatus status,
        int page,
        int size
    ) {
        return projectDraftQueryRepository.searchDrafts(userId, status, page, size);
    }
}
