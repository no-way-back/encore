package com.nowayback.funding.application.funding.dto.command;

import java.util.UUID;

public record GetProjectSponsorsCommand(
	UUID projectId,
	UUID creatorId,
	int page,
	int size
) {
}
