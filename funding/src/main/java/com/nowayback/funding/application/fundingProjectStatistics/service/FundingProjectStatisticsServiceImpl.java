package com.nowayback.funding.application.fundingProjectStatistics.service;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatistics;
import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatus;
import com.nowayback.funding.domain.fundingProjectStatistics.repository.FundingProjectStatisticsRepository;
import com.nowayback.funding.domain.fundingProjectStatistics.sevice.FundingProjectStatisticsService;

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

		FundingProjectStatistics stats = fundingProjectStatisticsRepository
			.findByProjectIdWithLock(projectId)
			.orElseThrow(() -> new FundingException(PROJECT_NOT_FOUND));

		validateProjectStatus(stats);

		stats.increaseFunding(amount);

		FundingProjectStatistics savedStatics = fundingProjectStatisticsRepository.save(stats);

		log.info("펀딩 통계 증가 완료 - projectId: {}, currentAmount: {}, participantCount: {}",
			projectId, savedStatics.getCurrentAmount(), savedStatics.getParticipantCount());
	}

	@Override
	@Transactional
	public void decreaseFundingStatusRate(UUID projectId, Long amount) {
		log.info("펀딩 통계 감소 시작 - projectId: {}, amount: {}", projectId, amount);

		FundingProjectStatistics stats = fundingProjectStatisticsRepository
			.findByProjectIdWithLock(projectId)
			.orElseThrow(() -> new FundingException(PROJECT_NOT_FOUND));

		stats.decreaseFunding(amount);

		fundingProjectStatisticsRepository.save(stats);

		log.info("펀딩 통계 감소 완료 - projectId: {}, currentAmount: {}, participantCount: {}",
			projectId, stats.getCurrentAmount(), stats.getParticipantCount());
	}

	private void validateProjectStatus(FundingProjectStatistics stats) {
		if (stats.getStatus() != FundingProjectStatus.PROCESSING) {
			log.warn("후원 불가능한 프로젝트 상태 - projectId: {}, status: {}",
				stats.getProjectId(), stats.getStatus());
			throw new FundingException(PROJECT_NOT_ONGOING);
		}

		if (LocalDateTime.now().isAfter(stats.getEndDate())) {
			log.warn("펀딩 기간 종료 - projectId: {}, endDate: {}",
				stats.getProjectId(), stats.getEndDate());
			throw new FundingException(PROJECT_FUNDING_PERIOD_ENDED);
		}
	}
}
