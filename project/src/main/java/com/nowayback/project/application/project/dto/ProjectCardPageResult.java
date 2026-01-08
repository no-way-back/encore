package com.nowayback.project.application.project.dto;

import java.util.List;

public record ProjectCardPageResult(
    List<ProjectCard> projectCards,
    Long count
) {

    public static ProjectCardPageResult of(
        List<ProjectCard> projectCards,
        Long count
    ) {
        return new ProjectCardPageResult(projectCards, count);
    }
}
