package com.nowayback.project.application.project;

public class PageLimitCalculator {
    public static Long calculatePageLimit(Long page, Long pageSize, Long movablePageCount) {
        return (((page -1) / movablePageCount) + 1) * pageSize * movablePageCount +1;
    }
}
