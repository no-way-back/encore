package com.nowayback.funding.domain.fundingProjectStatistics.sevice;

import java.util.UUID;

public interface FundingProjectStatisticsService {

	void increaseFundingStatusRate(UUID projectId, Long amount);

	void decreaseFundingStatusRate(UUID projectId, Long amount);
}
