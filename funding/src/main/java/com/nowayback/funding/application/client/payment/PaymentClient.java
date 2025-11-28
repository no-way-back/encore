package com.nowayback.funding.application.client.payment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nowayback.funding.application.client.payment.dto.request.ProcessPaymentRequest;
import com.nowayback.funding.application.client.payment.dto.response.ProcessPaymentResponse;

@FeignClient(name = "payment-service", url = "${feign.client.config.payment-service.url")
public interface PaymentClient {

	@PostMapping("/payments")
	ProcessPaymentResponse processPayment(
		@RequestBody ProcessPaymentRequest request
	);
}
