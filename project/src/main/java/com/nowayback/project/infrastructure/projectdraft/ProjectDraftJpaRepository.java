package com.nowayback.project.infrastructure.projectdraft;

import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import com.nowayback.project.domain.projectDraft.vo.ProjectDraftStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectDraftJpaRepository extends JpaRepository<ProjectDraft, UUID> {

    Optional<ProjectDraft> findByIdAndStatus(UUID projectDraftId, ProjectDraftStatus draft);
}
