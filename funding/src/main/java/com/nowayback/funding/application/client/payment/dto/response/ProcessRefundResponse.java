package com.nowayback.funding.application.client.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProcessRefundResponse(
	UUID paymentId
) {
}
