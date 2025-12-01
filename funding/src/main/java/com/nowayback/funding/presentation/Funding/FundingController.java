package com.nowayback.funding.presentation.Funding;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nowayback.funding.application.funding.dto.command.CancelFundingCommand;
import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;
import com.nowayback.funding.application.funding.dto.command.GetMyFundingsCommand;
import com.nowayback.funding.application.funding.dto.command.GetProjectSponsorsCommand;
import com.nowayback.funding.application.funding.dto.result.CancelFundingResult;
import com.nowayback.funding.application.funding.dto.result.CreateFundingResult;
import com.nowayback.funding.application.funding.dto.result.GetMyFundingsResult;
import com.nowayback.funding.application.funding.dto.result.GetProjectSponsorsResult;
import com.nowayback.funding.application.funding.service.FundingService;
import com.nowayback.funding.presentation.Funding.dto.request.CancelFundingRequest;
import com.nowayback.funding.presentation.Funding.dto.request.CreateFundingRequest;
import com.nowayback.funding.presentation.Funding.dto.request.GetMyFundingsRequest;
import com.nowayback.funding.presentation.Funding.dto.request.GetProjectSponsorsRequest;
import com.nowayback.funding.presentation.Funding.dto.response.CancelFundingResponse;
import com.nowayback.funding.presentation.Funding.dto.response.CreateFundingResponse;
import com.nowayback.funding.presentation.Funding.dto.response.GetMyFundingsResponse;
import com.nowayback.funding.presentation.Funding.dto.response.GetProjectSponsorsResponse;

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

		CreateFundingResult result = fundingService.createFunding(command);

		CreateFundingResponse response = CreateFundingResponse.from(result);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@DeleteMapping("/{fundingId}")
	public ResponseEntity<CancelFundingResponse> cancelFunding(
		@PathVariable UUID fundingId,
		@RequestHeader(value = "X-User-Id") UUID userId,
		@Validated @RequestBody CancelFundingRequest request
	) {
		CancelFundingCommand command = request.toCommand(fundingId, userId);

		CancelFundingResult result = fundingService.cancelFunding(command);

		CancelFundingResponse response = CancelFundingResponse.from(result);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/my")
	public ResponseEntity<GetMyFundingsResponse> getMyFundings(
		@RequestHeader(value = "X-User-Id") UUID userId,
		@Validated @ModelAttribute GetMyFundingsRequest request
	) {
		GetMyFundingsCommand command = request.toCommand(userId);

		GetMyFundingsResult result = fundingService.getMyFundings(command);

		GetMyFundingsResponse response = GetMyFundingsResponse.from(result);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/projects/{projectId}")
	public ResponseEntity<GetProjectSponsorsResponse> getProjectSponsors(
		@PathVariable UUID projectId,
		@RequestHeader(value = "X-User-Id") UUID creatorId,
		@Validated @ModelAttribute GetProjectSponsorsRequest request
	) {
		GetProjectSponsorsCommand command = request.toCommand(projectId, creatorId);

		GetProjectSponsorsResult result = fundingService.getProjectSponsors(command);

		GetProjectSponsorsResponse response = GetProjectSponsorsResponse.from(result);

		return ResponseEntity.ok(response);
	}
}
