package com.nowayback.funding.presentation.fundingProjectStatistics.dto.response;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.nowayback.funding.application.fundingProjectStatistics.dto.result.FundingProjectStatisticsResult;

public record FundingProjectStatisticsResponse(
	UUID projectId,
	Long targetAmount,
	Long currentAmount,
	Double achievementRate,
	Integer participantCount,
	LocalDateTime startDate,
	LocalDateTime endDate,
	Long remainingDays,
	String status
) {
	public static FundingProjectStatisticsResponse from(FundingProjectStatisticsResult result) {
		long remainingDays = calculateRemainingDays(result.endDate());

		return new FundingProjectStatisticsResponse(
			result.projectId(),
			result.targetAmount(),
			result.currentAmount(),
			result.achievementRate(),
			result.participantCount(),
			result.startDate(),
			result.endDate(),
			remainingDays,
			result.status()
		);
	}

	private static long calculateRemainingDays(LocalDateTime endDate) {
		LocalDateTime now = LocalDateTime.now();
		if (now.isAfter(endDate)) {
			return 0L;
		}
		return ChronoUnit.DAYS.between(now, endDate);
	}
}
