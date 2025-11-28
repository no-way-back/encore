package com.nowayback.funding.application.funding.service;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;

import java.util.Map;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nowayback.funding.application.client.payment.PaymentClient;
import com.nowayback.funding.application.client.payment.dto.request.ProcessPaymentRequest;
import com.nowayback.funding.application.client.project.ProjectClient;
import com.nowayback.funding.application.client.reward.RewardClient;
import com.nowayback.funding.application.client.reward.dto.request.DecreaseRewardRequest;
import com.nowayback.funding.application.client.reward.dto.response.DecreaseRewardResponse;
import com.nowayback.funding.application.client.payment.dto.response.ProcessPaymentResponse;
import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;
import com.nowayback.funding.application.funding.dto.result.FundingResult;
import com.nowayback.funding.application.funding.event.dto.OutboxEventCreated;
import com.nowayback.funding.application.fundingProjectStatistics.service.FundingProjectStatisticsService;
import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.domain.funding.entity.Outbox;
import com.nowayback.funding.domain.funding.repository.FundingRepository;
import com.nowayback.funding.domain.funding.repository.OutboxRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FundingServiceImpl implements FundingService {

	private final FundingRepository fundingRepository;
	private final FundingProjectStatisticsService fundingProjectStatisticsService;
	private final RewardClient rewardClient;
	private final PaymentClient paymentClient;
	private final ProjectClient projectClient;
	private final OutboxRepository outboxRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	@Transactional
	public FundingResult createFunding(CreateFundingCommand command) {

		log.info("펀딩 시작 - projectId: {}, userId: {}, amount: {}",
			command.projectId(), command.userId(), command.amount());

		fundingRepository.findByIdempotencyKey(command.idempotencyKey())
			.ifPresent(existingFunding -> {
				log.warn("중복 요청 감지 - idempotencyKey: {}",
					command.idempotencyKey());
				throw new FundingException(DUPLICATE_REQUEST);
			});

		Funding funding = Funding.createFunding(
			command.projectId(),
			command.userId(),
			command.amount(),
			command.idempotencyKey()
		);

		Funding savedFunding = fundingRepository.save(funding);

		log.info("Funding 생성 완료 - funding: {}", savedFunding.getId());

		UUID reservationId = null;

		try {
			if (command.hasRewards()) {
				DecreaseRewardRequest rewardRequest = DecreaseRewardRequest.from(funding.getId(), command);
				DecreaseRewardResponse rewardResponse = rewardClient.decreaseReward(rewardRequest);
				reservationId = rewardResponse.reservationId();
				log.info("재고 감소 성공 - reservationId: {}", reservationId);
			}

			ProcessPaymentRequest paymentRequest = ProcessPaymentRequest.from(funding.getId(), command);
			ProcessPaymentResponse paymentResponse = paymentClient.processPayment(paymentRequest);
			UUID paymentId = paymentResponse.paymentId();
			log.info("결제 성공 - paymentId: {}", paymentId);

			funding.completeFunding(reservationId, paymentId);
			log.info("펀딩 완료 - funding: {}", funding.getId());

			fundingProjectStatisticsService.updateFundingStatusRate(command);

			return FundingResult.success(funding.getId());
		} catch (FundingException e) {
			log.error("외부 서비스 호출 실패 - funding: {}, error: {}", funding.getId(), e.getMessage(), e);

			handleFundingFailure(funding, reservationId, e.getMessage());

			throw new FundingException(FUNDING_PROCESS_FAILED);

		} catch (Exception e) {
			log.error("예상치 못한 오류 - funding: {}, error: {}", funding.getId(), e.getMessage(), e);

			handleFundingFailure(funding, reservationId, e.getMessage());

			throw new FundingException(FUNDING_PROCESS_FAILED);
		}
	}

	private void handleFundingFailure(Funding funding, UUID reservationId, String errorMessage) {
		funding.failFunding(errorMessage);

		if (reservationId != null) {
			Outbox event = Outbox.createOutbox(
				"FUNDING",
				null,
				"REWARD_CANCELLATION_REQUESTED",
				Map.of("reservationId", reservationId)
			);

			Outbox savedOutbox = outboxRepository.save(event);
			eventPublisher.publishEvent(new OutboxEventCreated(savedOutbox.getId()));
		}
	}
}
