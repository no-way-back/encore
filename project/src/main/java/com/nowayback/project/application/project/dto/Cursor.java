package com.nowayback.project.application.project.dto;

import com.nowayback.project.domain.project.vo.ProjectSortType;

public record Cursor(
    ProjectSortType sortType,
    Long page,
    Long size
) {

    public Cursor(ProjectSortType sortType, Long page, Long size) {
        long safePage = Math.max(page, 0);
        long safeSize = Math.min(Math.max(size, 1), 100);

        this.sortType = sortType;
        this.page = safePage;
        this.size = safeSize;
    }

    public Long getOffset() {
        return (this.page -1) * this.size;
    }
}
