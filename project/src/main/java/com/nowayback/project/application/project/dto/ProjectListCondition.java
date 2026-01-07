package com.nowayback.project.application.project.dto;

import com.nowayback.project.domain.project.vo.ProjectSortType;
import com.nowayback.project.domain.project.vo.ProjectStatus;
import java.util.HashSet;
import java.util.Set;

public record ProjectListCondition(
    String rootCategoryCode,
    String categoryCode,
    ProjectSortType sort,
    Set<ProjectStatus> statuses
) {

    public static ProjectListCondition from(Cursor cursor, CategoryCodes categoryCodes, ProjectListState state) {
        Set<ProjectStatus> statuses = new HashSet<>();

        if (state == ProjectListState.ONGOING) {
            statuses.add(ProjectStatus.LIVE);
        }

        if (state == ProjectListState.CLOSED) {
            statuses.add(ProjectStatus.ENDED);
            statuses.add(ProjectStatus.FAIL);
            statuses.add(ProjectStatus.SUCCESS);
        }
        return new ProjectListCondition(
            categoryCodes.rootCategoryCode(),
            categoryCodes.categoryCode(),
            cursor.sortType(),
            statuses
        );
    }
}
