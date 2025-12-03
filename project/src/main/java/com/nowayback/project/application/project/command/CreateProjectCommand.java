package com.nowayback.project.application.project.command;

import com.nowayback.project.domain.project.vo.Account;
import java.time.LocalDate;
import java.util.UUID;

public record CreateProjectCommand(
     UUID userId,
     UUID projectDraftId,
     String title,
     String summary,
     String category,
     String thumbnailUrl,
     String contentHtml,
     Long goalAmount,
     LocalDate fundingStartDate,
     LocalDate fundingEndDate,
     Account account
) {
    public static CreateProjectCommand of(
        UUID userId,
        UUID projectDraftId,
        String title,
        String summary,
        String category,
        String thumbnailUrl,
        String contentHtml,
        Long goalAmount,
        LocalDate fundingStartDate,
        LocalDate fundingEndDate,
        Account account
    ) {
        return new CreateProjectCommand(
            userId,
            projectDraftId,
            title,
            summary,
            category,
            thumbnailUrl,
            contentHtml,
            goalAmount,
            fundingStartDate,
            fundingEndDate,
            account
        );
    }
}
