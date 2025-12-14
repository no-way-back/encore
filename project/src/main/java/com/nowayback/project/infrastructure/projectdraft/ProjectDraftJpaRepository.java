package com.nowayback.project.infrastructure.projectdraft;

import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectDraftJpaRepository extends JpaRepository<ProjectDraft, UUID> {

    List<ProjectDraft> findByUserId(UUID userId);
}
