package com.nowayback.project.application.project.dto;

import com.nowayback.project.domain.project.vo.ProjectSortType;

public record Cursor(
    ProjectSortType sortType,
    int page,
    int size
) {

    public Cursor(ProjectSortType sortType, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        this.sortType = sortType;
        this.page = safePage;
        this.size = safeSize;
    }
}
