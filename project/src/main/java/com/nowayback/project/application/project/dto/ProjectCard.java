package com.nowayback.project.application.project.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectCard(
    UUID projectId,
    String title,
    String thumbnailUrl,
    Long goalAmount,
    Long pledgedAmount,
    Integer backers,
    LocalDate endDate,
    BigDecimal popularityScore
) {}
