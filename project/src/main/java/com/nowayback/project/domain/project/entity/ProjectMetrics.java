package com.nowayback.project.domain.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_project_metrics")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMetrics {

    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "pledged_amount", nullable = false)
    private long pledgedAmount;

    @Column(name = "backers", nullable = false)
    private int backers;

    @Column(name = "likes", nullable = false)
    private int likes;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
