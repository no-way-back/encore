package com.nowayback.project.application.dto;

import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import java.util.UUID;

public record ProjectStoryDraftResult(
    UUID projectDraftId,
    String title,
    String summary,
    String category,
    String thumbnailUrl,
    String contentJson
) {
    public static ProjectStoryDraftResult of(ProjectDraft projectDraft) {
        return new ProjectStoryDraftResult(
            projectDraft.getId(),
            projectDraft.getStoryDraft().getTitle(),
            projectDraft.getStoryDraft().getSummary(),
            projectDraft.getStoryDraft().getCategory(),
            projectDraft.getStoryDraft().getThumbnailUrl(),
            projectDraft.getStoryDraft().getContentJson()
        );
    }
}
