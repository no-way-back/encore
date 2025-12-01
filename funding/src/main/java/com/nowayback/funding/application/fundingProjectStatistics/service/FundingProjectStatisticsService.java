package com.nowayback.funding.application.fundingProjectStatistics.service;

import java.util.UUID;

import com.nowayback.funding.application.fundingProjectStatistics.dto.result.FundingProjectStatisticsResult;

public interface FundingProjectStatisticsService {

	void increaseFundingStatusRate(UUID projectId, Long amount);

	void decreaseFundingStatusRate(UUID projectId, Long amount);

	FundingProjectStatisticsResult getFundingProjectStatistics(UUID projectId);

	void startScheduledProjects();

	void closeProcessingProjects();
}
