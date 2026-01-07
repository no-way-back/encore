package com.nowayback.project.application.project.dto;

import com.nowayback.project.domain.project.vo.ProjectStatus;
import java.util.Set;

public enum ProjectListState {
    ONGOING,
    CLOSED,
    ALL
    ;

    public static Set<ProjectStatus> toProjectStatuses(ProjectListState state) {
        if (state == null) {
            return Set.of();
        }

        return switch (state) {
            case ONGOING -> Set.of(ProjectStatus.LIVE);
            case CLOSED -> Set.of(
                ProjectStatus.ENDED,
                ProjectStatus.FAIL,
                ProjectStatus.SUCCESS
            );
            case ALL -> Set.of();
        };
    }
}
