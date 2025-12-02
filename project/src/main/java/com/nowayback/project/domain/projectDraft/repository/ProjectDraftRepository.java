package com.nowayback.project.domain.projectDraft.repository;

import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import com.nowayback.project.domain.projectDraft.vo.ProjectDraftStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface ProjectDraftRepository {

    ProjectDraft save(ProjectDraft projectDraft);

    Optional<ProjectDraft> findById(UUID projectDraftId);

    List<ProjectDraft> findByUserId(UUID userId);
    Page<ProjectDraft> searchDrafts(
        UUID userId,
        ProjectDraftStatus status,
        int page,
        int size
    );
}
