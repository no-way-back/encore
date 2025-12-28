package com.nowayback.funding.application.funding.dto.event.payload;

import java.util.UUID;

public record FundingRefundPayload(
        UUID fundingId
) {}