package com.nowayback.funding.application.client.payment;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.nowayback.funding.application.client.payment.dto.request.ProcessPaymentRequest;
import com.nowayback.funding.application.client.payment.dto.response.ProcessPaymentResponse;
import com.nowayback.funding.application.client.payment.dto.request.ProcessRefundRequest;
import com.nowayback.funding.application.client.payment.dto.response.ProcessRefundResponse;
import com.nowayback.funding.application.client.payment.dto.response.SettlementResponse;

@FeignClient(
	name = "payment-service",
	url = "${feign.client.config.payment-service.url}"
)
public interface PaymentClient {

	@PostMapping("/internal/payments/confirm")
	ProcessPaymentResponse processPayment(
		@RequestHeader("X-User-Id") UUID userId,
		@RequestBody ProcessPaymentRequest request
	);
 
	@PostMapping("/internal/payments/refund")
	ProcessRefundResponse processRefund(
		@RequestBody ProcessRefundRequest request
	);

	@PostMapping("/internal/settlements/{projectId}")
	SettlementResponse requestSettlement(
		@PathVariable UUID projectId
	);
}
