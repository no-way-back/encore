package com.nowayback.project.presentation.projectdraft.dto.request;

import com.nowayback.project.application.projectdraft.command.SaveStoryDraftCommand;
import java.util.UUID;

public record SaveProjectStoryDraftRequest(
    String title,
    String summary,
    String category,
    String thumbnailUrl,
    String contentJson
) {

    public SaveStoryDraftCommand toCommand(UUID draftId, UUID userId) {
        return SaveStoryDraftCommand.of(
            draftId,
            userId,
            title,
            summary,
            category,
            thumbnailUrl,
            contentJson
        );
    }
}
