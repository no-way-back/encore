package com.nowayback.project.infrastructure.project.persistence;

import com.nowayback.project.domain.project.entity.ProjectStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectStatusHistoryJpaRepository extends
    JpaRepository<ProjectStatusHistory, Long> {

}
