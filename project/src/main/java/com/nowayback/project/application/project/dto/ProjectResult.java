package com.nowayback.project.application.project.dto;

import com.nowayback.project.domain.project.entity.Project;
import com.nowayback.project.domain.project.vo.ProjectStatus;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectResult(
    UUID projectId,
    UUID userId,
    String title,
    String summary,
    String category,
    String thumbnailUrl,
    String contentHtml,
    Long goalAmount,
    LocalDate fundingStartDate,
    LocalDate fundingEndDate,
    ProjectStatus status
) {

    public static ProjectResult of(Project project) {
        return new ProjectResult(
            project.getId(),
            project.getUserId(),
            project.getTitle(),
            project.getSummary(),
            project.getCategory(),
            project.getThumbnailUrl(),
            project.getContentHtml(),
            project.getGoalAmount(),
            project.getFundingStartDate(),
            project.getFundingEndDate(),
            project.getStatus()
        );
    }
}
