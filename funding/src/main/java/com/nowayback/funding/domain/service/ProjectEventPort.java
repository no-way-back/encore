package com.nowayback.funding.domain.service;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ProjectEventPort {

	void handleProjectCreated(
		UUID projectId,
		Long targetAmount,
		LocalDateTime startDate,
		LocalDateTime endDate
	);
}
