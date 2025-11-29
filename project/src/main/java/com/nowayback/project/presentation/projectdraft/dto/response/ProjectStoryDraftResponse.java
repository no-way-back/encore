package com.nowayback.project.presentation.projectdraft.dto.response;

import com.nowayback.project.application.projectdraft.dto.ProjectStoryDraftResult;
import java.util.UUID;

public record ProjectStoryDraftResponse(
    UUID projectDraftId,
    String title,
    String summary,
    String category,
    String thumbnailUrl,
    String contentJson
) {
    public static ProjectStoryDraftResponse from(ProjectStoryDraftResult result) {
        return new ProjectStoryDraftResponse(
            result.projectDraftId(),
            result.title(),
            result.summary(),
            result.category(),
            result.thumbnailUrl(),
            result.contentJson()
        );
    }
}
