package com.nowayback.reward.infrastructure.client.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectResponse(
        UUID projectId,
        UUID userId,
        String title,
        String summary,
        String category,
        String thumbnailUrl,
        String contentHtml,
        Long goalAmount,
        LocalDate fundingStartDate,
        LocalDate fundingEndDate,
        String status
) {}