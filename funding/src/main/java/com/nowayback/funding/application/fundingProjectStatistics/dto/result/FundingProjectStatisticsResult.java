package com.nowayback.funding.application.fundingProjectStatistics.dto.result;

import java.time.LocalDateTime;
import java.util.UUID;

import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatistics;

public record FundingProjectStatisticsResult(
	UUID projectId,
	Long targetAmount,
	Long currentAmount,
	Double achievementRate,
	Integer participantCount,
	LocalDateTime startDate,
	LocalDateTime endDate,
	String status
) {
	public static FundingProjectStatisticsResult from(FundingProjectStatistics statistics) {
		return new FundingProjectStatisticsResult(
			statistics.getProjectId(),
			statistics.getTargetAmount(),
			statistics.getCurrentAmount(),
			statistics.getAchievementRate(),
			statistics.getParticipantCount(),
			statistics.getStartDate(),
			statistics.getEndDate(),
			statistics.getStatus().name()
		);
	}
}
