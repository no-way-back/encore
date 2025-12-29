package com.nowayback.funding.application.fundingProjectStatistics.dto.event.payload;

import java.util.UUID;

public record ProjectFundingFailedPayload(
        UUID projectId,
        Long finalAmount,
        Integer participantCount,
        Long targetAmount,
        Double achievementRate
) {}