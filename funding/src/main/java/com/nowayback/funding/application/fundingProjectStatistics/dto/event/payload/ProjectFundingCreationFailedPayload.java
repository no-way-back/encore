package com.nowayback.funding.application.fundingProjectStatistics.dto.event.payload;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProjectFundingCreationFailedPayload(
        UUID projectId,
        UUID creatorId,
        Long targetAmount,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String failureReason
) {}