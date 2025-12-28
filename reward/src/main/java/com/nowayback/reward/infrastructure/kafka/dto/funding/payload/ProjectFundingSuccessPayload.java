package com.nowayback.reward.infrastructure.kafka.dto.funding.payload;

import java.util.UUID;

public record ProjectFundingSuccessPayload(
        UUID projectId
) {}