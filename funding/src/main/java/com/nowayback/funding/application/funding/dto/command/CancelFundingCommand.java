package com.nowayback.funding.application.funding.dto.command;

import java.util.UUID;

public record CancelFundingCommand(
	UUID fundingId,
	UUID userId,
	String reason
) {
}
