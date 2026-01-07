package com.nowayback.project.application.projectdraft.dto;

import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import java.util.UUID;

public record ProjectStoryDraftResult(
    UUID projectDraftId,
    String title,
    String summary,
    UUID categoryId,
    UUID rootCategoryId,
    String thumbnailUrl,
    String contentJson
) {
    public static ProjectStoryDraftResult of(ProjectDraft projectDraft) {
        return new ProjectStoryDraftResult(
            projectDraft.getId(),
            projectDraft.getStoryDraft().getTitle(),
            projectDraft.getStoryDraft().getSummary(),
            projectDraft.getStoryDraft().getCategoryId(),
            projectDraft.getStoryDraft().getRootCategoryId(),
            projectDraft.getStoryDraft().getThumbnailUrl(),
            projectDraft.getStoryDraft().getContentJson()
        );
    }
}
