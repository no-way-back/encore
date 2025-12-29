package com.nowayback.funding.application.client.payment.dto.request;

import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;

import java.util.UUID;

public record ProcessPaymentRequest(
	UUID fundingId,
	UUID projectId,
	Long amount,
	String pgPaymentKey,
	String pgOrderId,
	String pgMethod
) {
	public static ProcessPaymentRequest from(UUID fundingId, Long totalAmount, CreateFundingCommand command) {
		return new ProcessPaymentRequest(
			fundingId,
			command.projectId(),
			totalAmount,
			command.pgPaymentKey(),
			command.pgOrderId(),
			command.pgMethod()
		);
	}
}