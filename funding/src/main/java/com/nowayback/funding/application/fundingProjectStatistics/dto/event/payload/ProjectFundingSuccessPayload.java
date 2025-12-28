package com.nowayback.funding.application.fundingProjectStatistics.dto.event.payload;

import java.util.UUID;

public record ProjectFundingSuccessPayload(
        UUID projectId,
        Long finalAmount,
        Long participantCount
) {}