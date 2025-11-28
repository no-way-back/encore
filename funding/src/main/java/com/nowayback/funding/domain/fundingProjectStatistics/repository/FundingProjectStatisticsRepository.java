package com.nowayback.funding.domain.fundingProjectStatistics.repository;

import java.util.Optional;
import java.util.UUID;

import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatistics;

public interface FundingProjectStatisticsRepository {

	FundingProjectStatistics save(FundingProjectStatistics statistics);

	Optional<FundingProjectStatistics> findByProjectId(UUID projectId);

	Optional<FundingProjectStatistics> findByProjectIdWithLock(UUID projectId);
}
