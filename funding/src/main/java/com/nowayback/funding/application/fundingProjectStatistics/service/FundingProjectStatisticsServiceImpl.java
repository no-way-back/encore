package com.nowayback.funding.application.fundingProjectStatistics.service;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nowayback.funding.application.fundingProjectStatistics.dto.result.FundingProjectStatisticsResult;
import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatistics;
import com.nowayback.funding.domain.fundingProjectStatistics.repository.FundingProjectStatisticsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FundingProjectStatisticsServiceImpl implements FundingProjectStatisticsService {

	private final FundingProjectStatisticsRepository fundingProjectStatisticsRepository;

	public FundingProjectStatisticsServiceImpl(FundingProjectStatisticsRepository fundingProjectStatisticsRepository) {
		this.fundingProjectStatisticsRepository = fundingProjectStatisticsRepository;
	}

	@Override
	@Transactional
	public void increaseFundingStatusRate(UUID projectId, Long amount) {
		log.info("펀딩 통계 증가 시작 - projectId: {}, amount: {}", projectId, amount);

		FundingProjectStatistics stats = getProjectStatisticsWithLock(projectId);

		stats.validateProjectStatusForCanFund();

		stats.increaseFunding(amount);

		FundingProjectStatistics savedStatics = fundingProjectStatisticsRepository.save(stats);

		log.info("펀딩 통계 증가 완료 - projectId: {}, currentAmount: {}, participantCount: {}",
			projectId, savedStatics.getCurrentAmount(), savedStatics.getParticipantCount());
	}

	@Override
	@Transactional
	public void decreaseFundingStatusRate(UUID projectId, Long amount) {
		log.info("펀딩 통계 감소 시작 - projectId: {}, amount: {}", projectId, amount);

		FundingProjectStatistics stats = getProjectStatisticsWithLock(projectId);

		stats.validateProjectStatusForCanFund();

		stats.decreaseFunding(amount);

		fundingProjectStatisticsRepository.save(stats);

		log.info("펀딩 통계 감소 완료 - projectId: {}, currentAmount: {}, participantCount: {}",
			projectId, stats.getCurrentAmount(), stats.getParticipantCount());
	}

	@Override
	@Transactional(readOnly = true)
	public FundingProjectStatisticsResult getFundingProjectStatistics(UUID projectId) {
		log.info("펀딩 현황 조회 - projectId: {}", projectId);

		FundingProjectStatistics stats = getProjectStatistics(projectId);

		return FundingProjectStatisticsResult.from(stats);
	}

	private FundingProjectStatistics getProjectStatistics(UUID projectId) {
		return fundingProjectStatisticsRepository
			.findByProjectId(projectId)
			.orElseThrow(() -> new FundingException(PROJECT_NOT_FOUND));
	}

	private FundingProjectStatistics getProjectStatisticsWithLock(UUID projectId) {
		return fundingProjectStatisticsRepository
			.findByProjectIdWithLock(projectId)
			.orElseThrow(() -> new FundingException(PROJECT_NOT_FOUND));
	}
}
