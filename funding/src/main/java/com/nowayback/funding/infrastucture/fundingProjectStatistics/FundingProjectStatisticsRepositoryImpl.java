package com.nowayback.funding.infrastucture.fundingProjectStatistics;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatistics;
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
}
