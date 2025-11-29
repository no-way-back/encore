package com.nowayback.funding.application.client.reward.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DecreaseRewardResponse(
	UUID reservationId
) {}
