package com.nowayback.funding.application.fundingProjectStatistics.service;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;
import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatistics;
import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatus;
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
	public void updateFundingStatusRate(CreateFundingCommand command) {
		log.info("펀딩 통계 업데이트 시작 - projectId: {}, amount: {}",
			command.projectId(), command.amount());

		FundingProjectStatistics stats = fundingProjectStatisticsRepository
			.findByProjectIdWithLock(command.projectId())
			.orElseThrow(() -> new FundingException(PROJECT_NOT_FOUND));

		validateProjectStatus(stats);

		stats.increaseFunding(command.amount());

		FundingProjectStatistics savedStatics = fundingProjectStatisticsRepository.save(stats);

		log.info("펀딩 통계 업데이트 완료 - projectId: {}, currentAmount: {}, participantCount: {}",
			command.projectId(), savedStatics.getCurrentAmount(), savedStatics.getParticipantCount());
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
