package com.nowayback.project.application.command;

import java.util.UUID;

public record SaveStoryDraftCommand(
    UUID projectDraftId,
    UUID userId,
    String title,
    String summary,
    String category,
    String thumbnailUrl,
    String contentJson
) {

    public static SaveStoryDraftCommand of(
        UUID projectDraftId,
        UUID userId,
        String title,
        String summary,
        String category,
        String thumbnailUrl,
        String contentJson
    ) {
        return new SaveStoryDraftCommand(
            projectDraftId,
            userId,
            title,
            summary,
            category,
            thumbnailUrl,
            contentJson
        );
    }
}
