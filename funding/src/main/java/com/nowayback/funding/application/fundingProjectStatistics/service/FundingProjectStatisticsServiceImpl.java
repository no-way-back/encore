package com.nowayback.funding.application.fundingProjectStatistics.service;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nowayback.funding.application.client.payment.PaymentClient;
import com.nowayback.funding.application.client.payment.dto.response.SettlementResponse;
import com.nowayback.funding.application.fundingProjectStatistics.dto.result.FundingProjectStatisticsResult;
import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.event.OutboxEventCreated;
import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatistics;
import com.nowayback.funding.domain.fundingProjectStatistics.repository.FundingProjectStatisticsRepository;
import com.nowayback.funding.domain.outbox.entity.Outbox;
import com.nowayback.funding.domain.outbox.repository.OutboxRepository;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FundingProjectStatisticsServiceImpl implements FundingProjectStatisticsService {

	private final FundingProjectStatisticsRepository fundingProjectStatisticsRepository;
	private final PaymentClient paymentClient;
	private final OutboxRepository outboxRepository;
	private final ApplicationEventPublisher eventPublisher;

	public FundingProjectStatisticsServiceImpl(FundingProjectStatisticsRepository fundingProjectStatisticsRepository,
		PaymentClient paymentClient, OutboxRepository outboxRepository, ApplicationEventPublisher eventPublisher) {
		this.fundingProjectStatisticsRepository = fundingProjectStatisticsRepository;
		this.paymentClient = paymentClient;
		this.outboxRepository = outboxRepository;
		this.eventPublisher = eventPublisher;
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
			try {
				project.startProject();
				fundingProjectStatisticsRepository.save(project);

				log.info("프로젝트 시작 완료 - projectId: {}, status: SCHEDULED → PROCESSING",
					project.getProjectId());

			} catch (FundingException e) {
				log.error("프로젝트 시작 실패 - projectId: {}, reason: {}",
					project.getProjectId(), e.getMessage());
			} catch (Exception e) {
				log.error("프로젝트 시작 중 예상치 못한 오류 - projectId: {}",
					project.getProjectId(), e);
			}
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
			try {
				if (project.isTargetAchieved()) {
					project.markAsSettlementInProgress();
					fundingProjectStatisticsRepository.save(project);

					log.info("프로젝트 목표 달성 - 정산 시작 - projectId: {}, 달성률: {}%",
						project.getProjectId(),
						String.format("%.2f", project.getAchievementRate()));
					try {
						log.info("정산 요청 시작 - projectId: {}", project.getProjectId());

						SettlementResponse response =
							paymentClient.requestSettlement(project.getProjectId());

						log.info("정산 완료 - projectId: {}, settlementId: {}, status: {}",
							project.getProjectId(), response.settlementId(), response.status());

						project.markAsSuccess();
						fundingProjectStatisticsRepository.save(project);

						log.info("프로젝트 성공 처리 완료 - projectId: {}, status: SETTLEMENT_IN_PROGRESS → SUCCESS",
							project.getProjectId());

						Outbox event = Outbox.createOutbox(
							"FUNDING_PROJECT",
							project.getProjectId(),
							"PROJECT_FUNDING_SUCCESS",
							Map.of(
								"projectId", project.getProjectId(),
								"finalAmount", project.getCurrentAmount(),
								"participantCount", project.getParticipantCount()
							)
						);

						outboxRepository.save(event);

						// TODO: Kafka 를 통해 프로젝트 서비스, 리워드 서비스에게 알림 -> 리스너 구현 예정
						eventPublisher.publishEvent(new OutboxEventCreated(event.getId()));

						log.info("프로젝트 성공 이벤트 발행 - projectId: {}", project.getProjectId());

					} catch (FeignException e) {
						log.error("정산 요청 실패 - projectId: {}, httpStatus: {}",
							project.getProjectId(), e.status(), e);

					} catch (Exception e) {
						log.error("정산 요청 중 예상치 못한 오류 - projectId: {}",
							project.getProjectId(), e);
					}

				} else {
					project.markAsRefundInProgress();
					fundingProjectStatisticsRepository.save(project);

					log.info("프로젝트 실패 - 환불 진행 시작 - projectId: {}, 달성률: {}%",
						project.getProjectId(),
						String.format("%.2f", project.getAchievementRate()));

					// TODO: 환불 이벤트 발행 예정, 또한 성공했을 때 응답을 받아서 FundingProjectFailed 이벤트 발행하여 프로젝트에게 상태변경 알림
				}

			} catch (FundingException e) {
				log.error("프로젝트 종료 실패 - projectId: {}, reason: {}",
					project.getProjectId(), e.getMessage());
			} catch (Exception e) {
				log.error("프로젝트 종료 중 예상치 못한 오류 - projectId: {}",
					project.getProjectId(), e);
			}
		}
	}
}
