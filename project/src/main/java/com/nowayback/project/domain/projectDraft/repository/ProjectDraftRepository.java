package com.nowayback.project.domain.projectDraft.repository;

import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import java.util.Optional;
import java.util.UUID;

public interface ProjectDraftRepository {

    ProjectDraft save(ProjectDraft projectDraft);

    Optional<ProjectDraft> findById(UUID projectDraftId);

}
