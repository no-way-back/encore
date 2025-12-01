package com.nowayback.funding.application.client.payment.dto.request;

import java.util.UUID;

public record ProcessRefundRequest(
	UUID paymentId,
	String reason
) {
	public static ProcessRefundRequest of(UUID paymentId, String reason) {
		return new ProcessRefundRequest(paymentId, reason);
	}
}
