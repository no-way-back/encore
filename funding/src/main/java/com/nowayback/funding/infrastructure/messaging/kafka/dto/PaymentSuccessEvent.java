package com.nowayback.funding.infrastructure.messaging.kafka.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentSuccessEvent(
	UUID fundingId,
	UUID paymentId
) {}
