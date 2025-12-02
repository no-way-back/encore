package com.nowayback.project.presentation.project.dto.response;

import com.nowayback.project.application.project.dto.ProjectResult;
import com.nowayback.project.domain.project.vo.ProjectStatus;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectResponse(
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

    public static ProjectResponse from(ProjectResult result) {
        return new ProjectResponse(
            result.projectId(),
            result.userId(),
            result.title(),
            result.summary(),
            result.category(),
            result.thumbnailUrl(),
            result.contentHtml(),
            result.goalAmount(),
            result.fundingStartDate(),
            result.fundingEndDate(),
            result.status()
        );
    }
}
