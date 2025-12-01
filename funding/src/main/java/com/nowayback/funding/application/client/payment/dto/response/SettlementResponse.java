package com.nowayback.funding.application.client.payment.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SettlementResponse(
	UUID settlementId,
	UUID projectId,
	Long totalAmount,
	Long serviceFee,
	Long pgFee,
	Long settlementAmount,
	String accountBank,
	String accountNumber,
	String accountOwner,
	String status,
	LocalDateTime requestedAt,
	LocalDateTime completedAt
) {}
