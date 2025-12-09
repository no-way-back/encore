package com.nowayback.reward.infrastructure.kafka.dto.funding.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FundingRefundPayload(
        UUID fundingId
) {
}