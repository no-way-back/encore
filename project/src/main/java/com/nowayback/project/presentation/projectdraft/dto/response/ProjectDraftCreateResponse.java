package com.nowayback.project.presentation.projectdraft.response;

import java.util.UUID;

public record ProjectDraftCreateResponse(
    UUID projectDraftId
) {

    public static ProjectDraftCreateResponse of(UUID projectDraftId) {
        return new ProjectDraftCreateResponse(projectDraftId);
    }
}
