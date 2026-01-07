package com.nowayback.project.application.project.command;

import com.nowayback.project.domain.project.vo.Account;
import java.time.LocalDate;
import java.util.UUID;

public record CreateProjectCommand(
     UUID userId,
     UUID projectDraftId,
     String title,
     String summary,
     UUID categoryId,
     UUID rootCategoryId,
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
        UUID categoryId,
        UUID rootCategoryId,
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
            categoryId,
            rootCategoryId,
            thumbnailUrl,
            contentHtml,
            goalAmount,
            fundingStartDate,
            fundingEndDate,
            account
        );
    }
}
