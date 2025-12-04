package com.nowayback.funding.application.client.payment.dto.request;

import java.util.UUID;

import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;

public record ProcessPaymentRequest(
	UUID fundingId,
	UUID projectId,
	Long amount,
	String pgPaymentKey,
	String pgOrderId,
	String pgMethod
) {
	public static ProcessPaymentRequest from(UUID fundingId, CreateFundingCommand command) {
		return new ProcessPaymentRequest(
			fundingId,
			command.projectId(),
			command.amount(),
			command.pgPaymentKey(),
			command.pgOrderId(),
			command.pgMethod()
		);
	}
}