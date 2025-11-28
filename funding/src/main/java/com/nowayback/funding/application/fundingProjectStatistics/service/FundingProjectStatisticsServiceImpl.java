package com.nowayback.funding.application.fundingProjectStatistics.service;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;
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
	public void updateFundingStatusRate(CreateFundingCommand command) {
		log.info("펀딩 통계 업데이트 시작 - projectId: {}, amount: {}",
			command.projectId(), command.amount());

		FundingProjectStatistics stats = fundingProjectStatisticsRepository
			.findByProjectIdWithLock(command.projectId())
			.orElseThrow(() -> new FundingException(PROJECT_NOT_FOUND));

		stats.increaseFunding(command.amount());

		FundingProjectStatistics savedStatics = fundingProjectStatisticsRepository.save(stats);

		log.info("펀딩 통계 업데이트 완료 - projectId: {}, currentAmount: {}, participantCount: {}",
			command.projectId(), savedStatics.getCurrentAmount(), savedStatics.getParticipantCount());
	}
}
