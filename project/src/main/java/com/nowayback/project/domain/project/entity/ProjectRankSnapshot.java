package com.nowayback.project.domain.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "p_project_rank_snapshot")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectRankSnapshot {
    @Id
    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "computed_at", nullable = false)
    private LocalDateTime computedAt;

    @Column(name = "popularity_score", nullable = false, precision = 18, scale = 6)
    private BigDecimal popularityScore;
}
