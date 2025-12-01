package com.nowayback.funding.application.client.payment.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProcessRefundResponse(
	UUID paymentId
) {
}
