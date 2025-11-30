package com.nowayback.funding.presentation.Funding.dto.request;

import java.util.UUID;

import com.nowayback.funding.application.funding.dto.command.CancelFundingCommand;

import jakarta.validation.constraints.NotBlank;

public record CancelFundingRequest(
	@NotBlank(message = "취소 사유는 필수입니다.")
	String reason
) {
	public CancelFundingCommand toCommand(UUID fundingId, UUID userId) {
		return new CancelFundingCommand(
			fundingId,
			userId,
			reason
		);
	}
}
