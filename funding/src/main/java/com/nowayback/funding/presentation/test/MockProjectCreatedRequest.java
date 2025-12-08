package com.nowayback.funding.presentation.test;

import java.time.LocalDateTime;
import java.util.UUID;

public record MockProjectCreatedRequest(
	UUID projectId,
	UUID creatorId,
	Long targetAmount,
	LocalDateTime startDate,
	LocalDateTime endDate
) {
	public static MockProjectCreatedRequest createRandom() {
		LocalDateTime now = LocalDateTime.now();
		return new MockProjectCreatedRequest(
			UUID.randomUUID(),
			UUID.randomUUID(),
			(long) (Math.random() * 50000000 + 1000000), // 100만 ~ 5000만
			now,
			now.plusDays((long) (Math.random() * 60 + 30)) // 30~90일 후
		);
	}
}