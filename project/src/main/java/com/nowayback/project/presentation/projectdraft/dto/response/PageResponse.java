package com.nowayback.project.presentation.projectdraft.dto.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponse<T>(
    int currentPage,
    int pageSize,
    int totalPages,
    long totalElements,
    String sortBy,
    boolean isAsc,
    List<T> items
    ) {

    public static <T> PageResponse<T> fromPage(Page<T> page, String sortBy, boolean isAsc) {
        return new PageResponse<>(
            page.getNumber() + 1,
            page.getSize(),
            page.getTotalPages(),
            page.getTotalElements(),
            sortBy,
            isAsc,
            page.getContent()
        );
    }

    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(
            page.getNumber() + 1,
            page.getSize(),
            page.getTotalPages(),
            page.getTotalElements(),
            "created_at",
            false,
            page.getContent()
        );
    }
}
