package com.nowayback.funding.application.fundingProjectStatistics.service;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nowayback.funding.application.client.payment.PaymentClient;
import com.nowayback.funding.application.client.payment.dto.response.SettlementResponse;
import com.nowayback.funding.application.fundingProjectStatistics.dto.result.FundingProjectStatisticsResult;
import com.nowayback.funding.application.outbox.service.OutboxService;
import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatistics;
import com.nowayback.funding.domain.fundingProjectStatistics.repository.FundingProjectStatisticsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FundingProjectStatisticsServiceImpl implements FundingProjectStatisticsService {

	private final FundingProjectStatisticsRepository fundingProjectStatisticsRepository;
	private final PaymentClient paymentClient;
	private final OutboxService outboxService;

	public FundingProjectStatisticsServiceImpl(FundingProjectStatisticsRepository fundingProjectStatisticsRepository, PaymentClient paymentClient, OutboxService outboxService) {
		this.fundingProjectStatisticsRepository = fundingProjectStatisticsRepository;
		this.paymentClient = paymentClient;
		this.outboxService = outboxService;
	}

	@Override
	@Transactional
	public void increaseFundingStatusRate(UUID projectId, Long amount) {
		log.info("펀딩 통계 증가 시작 - projectId: {}, amount: {}", projectId, amount);

		FundingProjectStatistics stats = fundingProjectStatisticsRepository
			.findByProjectIdWithLock(projectId)
			.orElseThrow(() -> new FundingException(PROJECT_NOT_FOUND));

		stats.validateProjectStatusForCanFund();

		stats.increaseFunding(amount);

		FundingProjectStatistics savedStats = fundingProjectStatisticsRepository.save(stats);

		log.info("펀딩 통계 증가 완료 - projectId: {}, currentAmount: {}, participantCount: {}",
			savedStats.getProjectId(),
			savedStats.getCurrentAmount(),
			savedStats.getParticipantCount());
	}

	@Override
	@Transactional
	public void decreaseFundingStatusRate(UUID projectId, Long amount) {
		log.info("펀딩 통계 감소 시작 - projectId: {}, amount: {}", projectId, amount);

		FundingProjectStatistics stats = fundingProjectStatisticsRepository
			.findByProjectIdWithLock(projectId)
			.orElseThrow(() -> new FundingException(PROJECT_NOT_FOUND));

		stats.validateProjectStatusForCanFund();

		stats.decreaseFunding(amount);

		FundingProjectStatistics savedStats = fundingProjectStatisticsRepository.save(stats);

		log.info("펀딩 통계 감소 완료 - projectId: {}, currentAmount: {}, participantCount: {}",
			savedStats.getProjectId(),
			savedStats.getCurrentAmount(),
			savedStats.getParticipantCount());
	}

	@Override
	@Transactional(readOnly = true)
	public FundingProjectStatisticsResult getFundingProjectStatistics(UUID projectId) {
		log.info("펀딩 현황 조회 - projectId: {}", projectId);

		FundingProjectStatistics stats = fundingProjectStatisticsRepository
			.findByProjectId(projectId)
			.orElseThrow(() -> new FundingException(PROJECT_NOT_FOUND));

		return FundingProjectStatisticsResult.from(stats);
	}

	@Override
	public void startScheduledProjects() {
		log.info("프로젝트 시작 스케줄러 실행");

		List<FundingProjectStatistics> projects =
			fundingProjectStatisticsRepository.findScheduledProjectsToStart(LocalDateTime.now());

		if (projects.isEmpty()) {
			log.debug("시작할 프로젝트가 없습니다.");
			return;
		}

		log.info("시작할 프로젝트 {}개 발견", projects.size());

		for (FundingProjectStatistics project : projects) {
			project.startProject();
			fundingProjectStatisticsRepository.save(project);

			log.info("프로젝트 시작 완료 - projectId: {}, status: SCHEDULED → PROCESSING",
				project.getProjectId());
		}
	}

	@Override
	public void closeProcessingProjects() {
		log.info("프로젝트 종료 스케줄러 실행");

		List<FundingProjectStatistics> projects =
			fundingProjectStatisticsRepository.findProcessingProjectsToClose(LocalDateTime.now());

		if (projects.isEmpty()) {
			log.debug("종료할 프로젝트가 없습니다.");
			return;
		}

		log.info("종료할 프로젝트 {}개 발견", projects.size());

		for (FundingProjectStatistics project : projects) {
			if (project.isTargetAchieved()) {
				project.markAsSettlementInProgress();
				fundingProjectStatisticsRepository.save(project);

				log.info("프로젝트 목표 달성 - 정산 시작 - projectId: {}, 달성률: {}%",
					project.getProjectId(),
					String.format("%.2f", project.getAchievementRate()));

				SettlementResponse response =
					paymentClient.requestSettlement(project.getProjectId());

				log.info("정산 완료 - projectId: {}, settlementId: {}, status: {}",
					project.getProjectId(), response.settlementId(), response.status());

				project.markAsSuccess();
				fundingProjectStatisticsRepository.save(project);

				log.info("프로젝트 성공 처리 완료 - projectId: {}, status: SETTLEMENT_IN_PROGRESS → SUCCESS",
					project.getProjectId());

				outboxService.publishSuccessEvent(
					"FUNDING_PROJECT",
					project.getProjectId(),
					"PROJECT_FUNDING_SUCCESS",
					Map.of(
						"projectId", project.getProjectId(),
						"finalAmount", project.getCurrentAmount(),
						"participantCount", project.getParticipantCount()
					)
				);

			} else {
				project.markAsRefundInProgress();
				fundingProjectStatisticsRepository.save(project);

				log.info("프로젝트 실패 - 환불 진행 시작 - projectId: {}, 달성률: {}%",
					project.getProjectId(),
					String.format("%.2f", project.getAchievementRate()));

				outboxService.publishSuccessEvent(
					"FUNDING_PROJECT",
					project.getProjectId(),
					"PROJECT_FUNDING_FAILED",
					Map.of(
						"projectId", project.getProjectId(),
						"finalAmount", project.getCurrentAmount(),
						"participantCount", project.getParticipantCount(),
						"targetAmount", project.getTargetAmount(),
						"achievementRate", project.getAchievementRate()
					)
				);
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public void validateProjectCreator(UUID projectId, UUID creatorId) {
		log.debug("프로젝트 생성자 권한 검증 - projectId: {}, creatorId: {}",
			projectId, creatorId);

		FundingProjectStatistics stats = fundingProjectStatisticsRepository
			.findByProjectId(projectId)
			.orElseThrow(() -> new FundingException(PROJECT_NOT_FOUND));

		if (!stats.isCreator(creatorId)) {
			log.warn("프로젝트 생성자 권한 없음 - projectId: {}, requestUserId: {}, actualCreatorId: {}",
				projectId, creatorId, stats.getCreatorId());
			throw new FundingException(FORBIDDEN_PROJECT_ACCESS);
		}

		log.debug("프로젝트 생성자 권한 확인 완료 - projectId: {}", projectId);
	}

	@Override
	@Transactional
	public void createProjectStatistics(
		UUID projectId,
		UUID creatorId,
		Long targetAmount,
		LocalDateTime startDate,
		LocalDateTime endDate
	) {
		log.info("프로젝트 통계 생성 시작 - projectId: {}", projectId);

		if (fundingProjectStatisticsRepository.findByProjectId(projectId).isPresent()) {
			log.warn("이미 존재하는 프로젝트 통계 - projectId: {}", projectId);
			throw new FundingException(PROJECT_NOT_FOUND);
		}

		FundingProjectStatistics statistics = FundingProjectStatistics.create(
			projectId,
			creatorId,
			targetAmount,
			startDate,
			endDate
		);

		fundingProjectStatisticsRepository.save(statistics);

		log.info("프로젝트 통계 생성 완료 - projectId: {}", projectId);
	}
}
