package com.nowayback.funding.infrastructure.schedule;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nowayback.funding.application.fundingProjectStatistics.service.FundingProjectStatisticsService;
import com.nowayback.funding.infrastructure.aop.DistributedLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class FundingProjectStatisticsScheduler {

	private final FundingProjectStatisticsService fundingProjectStatisticsService;

	/**
	 * 프로젝트 시작 스케줄러
	 * SCHEDULED → PROCESSING
	 * 매일 새벽 4시 실행
	 */
	@Scheduled(cron = "0 0 4 * * *")
	@DistributedLock(
		key = "'scheduler:startScheduledProjects'",
		leaseTime = 30,
		timeUnit = TimeUnit.MINUTES,
		useTransaction = false
	)
	public void startScheduledProjects() {
		log.debug("프로젝트 시작 스케줄러 실행");

		try {
			fundingProjectStatisticsService.startScheduledProjects();
		} catch (Exception e) {
			log.error("프로젝트 시작 스케줄러 실행 중 오류 발생", e);
		}
	}

	/**
	 * 프로젝트 종료 스케줄러
	 * PROCESSING → SUCCESS/REFUND_IN_PROGRESS
	 * 매일 새벽 4시 실행
	 */
	@Scheduled(cron = "0 0 4 * * *")
	@DistributedLock(
		key = "'scheduler:closeOngoingProjects'",
		leaseTime = 30,
		timeUnit = TimeUnit.MINUTES,
		useTransaction = false
	)
	public void closeOngoingProjects() {
		log.info("프로젝트 종료 스케줄러 실행 - 매일 새벽 4시");

		try {
			fundingProjectStatisticsService.closeProcessingProjects();
		} catch (Exception e) {
			log.error("프로젝트 종료 스케줄러 실행 중 오류 발생", e);
		}
	}
}
