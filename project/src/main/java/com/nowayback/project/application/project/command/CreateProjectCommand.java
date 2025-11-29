package com.nowayback.project.application.project.command;

import java.time.LocalDate;
import java.util.UUID;

public record CreateProjectCommand(
     UUID userId,
     String title,
     String summary,
     String category,
     String thumbnailUrl,
     String contentHtml,
     Long goalAmount,
     LocalDate fundingStartDate,
     LocalDate fundingEndDate
) {
    public static CreateProjectCommand of(
        UUID userId,
        String title,
        String summary,
        String category,
        String thumbnailUrl,
        String contentHtml,
        Long goalAmount,
        LocalDate fundingStartDate,
        LocalDate fundingEndDate
    ) {
        return new CreateProjectCommand(
            userId,
            title,
            summary,
            category,
            thumbnailUrl,
            contentHtml,
            goalAmount,
            fundingStartDate,
            fundingEndDate
        );
    }
}
