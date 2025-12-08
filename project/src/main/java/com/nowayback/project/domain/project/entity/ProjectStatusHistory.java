package com.nowayback.project.domain.project.entity;

import com.nowayback.project.domain.project.vo.ProjectStatus;
import com.nowayback.project.domain.shard.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_project_status_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectStatusHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 20)
    private ProjectStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 20)
    private ProjectStatus toStatus;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Builder
    private ProjectStatusHistory(
        UUID projectId,
        ProjectStatus fromStatus,
        ProjectStatus toStatus,
        LocalDateTime occurredAt
    ) {
        this.projectId = projectId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.occurredAt = occurredAt;
    }

    public static ProjectStatusHistory create(
        UUID projectId,
        ProjectStatus fromStatus,
        ProjectStatus toStatus,
        LocalDateTime occurredAt
    ) {
        return ProjectStatusHistory.builder()
            .projectId(projectId)
            .fromStatus(fromStatus)
            .toStatus(toStatus)
            .occurredAt(occurredAt)
            .build();
    }
}