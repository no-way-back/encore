package com.nowayback.funding.presentation.funding;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nowayback.funding.application.funding.dto.result.FundingDetailResult;
import com.nowayback.funding.application.funding.service.FundingService;
import com.nowayback.funding.presentation.funding.dto.response.FundingDetailResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/fundings")
@RequiredArgsConstructor
public class FundingInternalController {

	private final FundingService fundingService;

	@GetMapping("/{fundingId}")
	public ResponseEntity<FundingDetailResponse> getFundingDetail(
		@PathVariable UUID fundingId
	) {
		FundingDetailResult result = fundingService.getFundingDetail(fundingId);
		return ResponseEntity.ok(FundingDetailResponse.from(result));
	}
}
