package com.nowayback.funding.application.fundingProjectStatistics.dto.event.payload;

import java.util.UUID;

public record ProjectFundingFailedPayload(
        UUID projectId,
        Long finalAmount,
        Long participantCount,
        Long targetAmount,
        Double achievementRate
) {}