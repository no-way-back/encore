package com.nowayback.funding.domain.fundingProjectStatistics.repository;

import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatistics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FundingProjectStatisticsRepository {

	FundingProjectStatistics save(FundingProjectStatistics statistics);

	Optional<FundingProjectStatistics> findByProjectId(UUID projectId);

	Optional<FundingProjectStatistics> findByProjectIdWithLock(UUID projectId);

	List<FundingProjectStatistics> findScheduledProjectsToStart(LocalDateTime now);

	List<FundingProjectStatistics> findProcessingProjectsToClose(LocalDateTime now);
}
