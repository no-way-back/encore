package com.nowayback.funding.infrastructure.fundingProjectStatistics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatistics;
import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatus;
import com.nowayback.funding.domain.fundingProjectStatistics.repository.FundingProjectStatisticsRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FundingProjectStatisticsRepositoryImpl implements FundingProjectStatisticsRepository {

	private final FundingProjectStatisticsJpaRepository jpaRepository;

	@Override
	public FundingProjectStatistics save(FundingProjectStatistics statistics) {
		return jpaRepository.save(statistics);
	}

	@Override
	public Optional<FundingProjectStatistics> findByProjectId(UUID projectId) {
		return jpaRepository.findByProjectId(projectId);
	}

	@Override
	public Optional<FundingProjectStatistics> findByProjectIdWithLock(UUID projectId) {
		return jpaRepository.findByProjectIdWithLock(projectId);
	}

	@Override
	public List<FundingProjectStatistics> findScheduledProjectsToStart(LocalDateTime now) {
		return jpaRepository.findByStatusAndStartDateBefore(
			FundingProjectStatus.SCHEDULED,
			now
		);
	}

	@Override
	public List<FundingProjectStatistics> findProcessingProjectsToClose(LocalDateTime now) {
		return jpaRepository.findByStatusAndEndDateBefore(
			FundingProjectStatus.PROCESSING,
			now
		);
	}
}
