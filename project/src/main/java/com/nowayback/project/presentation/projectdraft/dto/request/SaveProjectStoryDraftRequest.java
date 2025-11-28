package com.nowayback.project.presentation.projectdraft.request;

public record SaveProjectStoryDraftRequest(
    String title,
    String summary,
    String category,
    String thumbnailUrl,
    String contentJson
) {

}
