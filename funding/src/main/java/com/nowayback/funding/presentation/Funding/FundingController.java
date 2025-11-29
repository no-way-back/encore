package com.nowayback.funding.presentation.Funding;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;
import com.nowayback.funding.application.funding.dto.result.FundingResult;
import com.nowayback.funding.application.funding.service.FundingService;
import com.nowayback.funding.presentation.Funding.dto.request.CreateFundingRequest;
import com.nowayback.funding.presentation.Funding.dto.response.CreateFundingResponse;

@RestController
@RequestMapping("/fundings")
public class FundingController {

	private final FundingService fundingService;

	public FundingController(FundingService fundingService) {
		this.fundingService = fundingService;
	}

	@PostMapping
	public ResponseEntity<CreateFundingResponse> createFunding(
		@RequestHeader(value = "X-User-Id") UUID userId,
		@RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
		@Validated @RequestBody CreateFundingRequest request
	) {
		String finalIdempotencyKey = idempotencyKey != null ? idempotencyKey : UUID.randomUUID().toString();

		CreateFundingCommand command = request.toCommand(userId, finalIdempotencyKey);

		FundingResult result = fundingService.createFunding(command);

		CreateFundingResponse response = CreateFundingResponse.from(result);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
