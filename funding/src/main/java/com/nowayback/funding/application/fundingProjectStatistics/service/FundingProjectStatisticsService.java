package com.nowayback.funding.application.fundingProjectStatistics.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.nowayback.funding.application.fundingProjectStatistics.dto.result.FundingProjectStatisticsResult;

public interface FundingProjectStatisticsService {

	void increaseFundingStatusRate(UUID projectId, Long amount);

	void decreaseFundingStatusRate(UUID projectId, Long amount);

	FundingProjectStatisticsResult getFundingProjectStatistics(UUID projectId);

	void startScheduledProjects();

	void closeProcessingProjects();

	void validateProjectForFunding(UUID projectId);

	void validateProjectCreator(UUID projectId, UUID creatorId);

	void createProjectStatistics(UUID projectId, UUID creatorId, Long targetAmount, LocalDateTime startDate, LocalDateTime endDate);
}
