package com.nowayback.project.domain.project.vo;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class ProjectMetricId {
    private UUID projectId;
    private LocalDate metricDate;
}
