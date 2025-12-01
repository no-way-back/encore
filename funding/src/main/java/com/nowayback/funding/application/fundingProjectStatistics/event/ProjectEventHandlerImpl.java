package com.nowayback.funding.application.fundingProjectStatistics.event;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatistics;
import com.nowayback.funding.domain.fundingProjectStatistics.repository.FundingProjectStatisticsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectEventHandlerImpl implements com.nowayback.funding.domain.eventHandler.ProjectEventHandler {

	private final FundingProjectStatisticsRepository fundingProjectStatisticsRepository;

	@Override
	@Transactional
	public void handleProjectCreated(
		UUID projectId,
		Long targetAmount,
		LocalDateTime startDate,
		LocalDateTime endDate
	) {
		log.info("프로젝트 통계 생성 시작 - projectId: {}", projectId);

		if (fundingProjectStatisticsRepository.findByProjectId(projectId).isPresent()) {
			log.warn("이미 존재하는 프로젝트 통계 - projectId: {}", projectId);
			return;
		}

		FundingProjectStatistics statistics = FundingProjectStatistics.create(
			projectId,
			targetAmount,
			startDate,
			endDate
		);

		fundingProjectStatisticsRepository.save(statistics);

		log.info("프로젝트 통계 생성 완료 - projectId: {}", projectId);
	}
}
