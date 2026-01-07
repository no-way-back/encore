package com.nowayback.project.domain.project.entity;


import com.nowayback.project.domain.project.vo.ProjectMetricId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_project_metrics_daily")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMetricsDaily {

    @EmbeddedId
    private ProjectMetricId id;

    @Column(name = "pledged_amount", nullable = false)
    private long pledgedAmount;

    @Column(name = "backers", nullable = false)
    private int backers;

    @Column(name = "views_uv", nullable = false)
    private int viewsUv;

    @Column(name = "likes", nullable = false)
    private int likes;
}
