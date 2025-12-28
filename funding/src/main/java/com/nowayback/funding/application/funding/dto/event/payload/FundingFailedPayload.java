package com.nowayback.funding.application.funding.dto.event.payload;

import java.util.UUID;

public record FundingFailedPayload(
        UUID fundingId
) {}