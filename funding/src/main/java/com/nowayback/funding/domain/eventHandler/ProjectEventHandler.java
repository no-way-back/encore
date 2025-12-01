package com.nowayback.funding.domain.eventHandler;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ProjectEventHandler {

	void handleProjectCreated(
		UUID projectId,
		UUID creatorId,
		Long targetAmount,
		LocalDateTime startDate,
		LocalDateTime endDate
	);
}
