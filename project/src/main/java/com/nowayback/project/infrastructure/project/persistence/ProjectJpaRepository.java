package com.nowayback.project.infrastructure.project.persistence;

import com.nowayback.project.domain.project.entity.Project;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectJpaRepository extends JpaRepository<Project, UUID> {

}
