package com.nowayback.funding.application.funding.service;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nowayback.funding.application.client.payment.PaymentClient;
import com.nowayback.funding.application.client.payment.dto.request.ProcessPaymentRequest;
import com.nowayback.funding.application.client.payment.dto.request.ProcessRefundRequest;
import com.nowayback.funding.application.client.payment.dto.response.ProcessRefundResponse;
import com.nowayback.funding.application.client.reward.RewardClient;
import com.nowayback.funding.application.client.reward.dto.request.DecreaseRewardRequest;
import com.nowayback.funding.application.client.reward.dto.request.RewardDetailsRequest;
import com.nowayback.funding.application.client.reward.dto.response.DecreaseRewardResponse;
import com.nowayback.funding.application.client.payment.dto.response.ProcessPaymentResponse;
import com.nowayback.funding.application.client.reward.dto.response.RewardDetailResponse;
import com.nowayback.funding.application.funding.dto.command.CancelFundingCommand;
import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;
import com.nowayback.funding.application.funding.dto.command.GetMyFundingsCommand;
import com.nowayback.funding.application.funding.dto.result.CancelFundingResult;
import com.nowayback.funding.application.funding.dto.result.CreateFundingResult;
import com.nowayback.funding.application.funding.dto.result.GetMyFundingsResult;
import com.nowayback.funding.domain.event.OutboxEventCreated;
import com.nowayback.funding.application.fundingProjectStatistics.service.FundingProjectStatisticsService;
import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.domain.funding.entity.FundingStatus;
import com.nowayback.funding.domain.outbox.entity.Outbox;
import com.nowayback.funding.domain.funding.repository.FundingRepository;
import com.nowayback.funding.domain.outbox.repository.OutboxRepository;

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
	private final OutboxRepository outboxRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	@Transactional
	public CreateFundingResult createFunding(CreateFundingCommand command) {

		log.info("펀딩 시작 - projectId: {}, userId: {}",
			command.projectId(), command.userId());

		fundingRepository.findByIdempotencyKey(command.idempotencyKey())
			.ifPresent(existingFunding -> {
				log.warn("중복 요청 감지 - idempotencyKey: {}",
					command.idempotencyKey());
				throw new FundingException(DUPLICATE_REQUEST);
			});

		boolean alreadyFunded = fundingRepository.existsByUserIdAndProjectIdAndStatus(
			command.userId(),
			command.projectId(),
			FundingStatus.COMPLETED
		);

		if (alreadyFunded) {
			log.warn("중복 후원 감지 - userId: {}, projectId: {}",
				command.userId(), command.projectId());
			throw new FundingException(DUPLICATE_FUNDING);
		}

		long calculatedRewardPrice = 0;

		if (command.hasRewards()) {
			List<UUID> rewardIds = command.rewardItems().stream()
				.map(CreateFundingCommand.RewardItem::rewardId)
				.toList();

			List<RewardDetailResponse> rewardDetails =
				rewardClient.getRewardDetails(new RewardDetailsRequest(rewardIds));

			Map<UUID, RewardDetailResponse> rewardDetailMap = rewardDetails.stream()
				.collect(Collectors.toMap(RewardDetailResponse::rewardId, r -> r));

			for (CreateFundingCommand.RewardItem item : command.rewardItems()) {
				RewardDetailResponse detail = rewardDetailMap.get(item.rewardId());

				if (detail == null) {
					throw new FundingException(REWARD_NOT_FOUND);
				}

				calculatedRewardPrice += detail.price() * item.quantity();
			}
		}

		Long totalAmount = calculatedRewardPrice + command.amount();

			Funding funding = Funding.createFunding(
			command.projectId(),
			command.userId(),
			command.idempotencyKey(),
			totalAmount
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

			fundingProjectStatisticsService.increaseFundingStatusRate(funding.getProjectId(), funding.getAmount());

			// TODO: 펀딩 성공 이벤트 발행 -> 리워드 서비스에서 QR 생성 목적

			return CreateFundingResult.success(funding.getId());
		} catch (FundingException e) {
			log.error("외부 서비스 호출 실패 - funding: {}, error: {}", funding.getId(), e.getMessage(), e);

			handleFundingFailure(funding, reservationId, e.getMessage());

			return CreateFundingResult.failure(e.getErrorCode().getMessage());
		} catch (Exception e) {
			log.error("예상치 못한 오류 - funding: {}, error: {}", funding.getId(), e.getMessage(), e);

			handleFundingFailure(funding, reservationId, e.getMessage());

			return CreateFundingResult.failure("후원 처리 중 오류가 발생했습니다.");
		}
	}

	@Override
	@Transactional
	public CancelFundingResult cancelFunding(CancelFundingCommand command) {
		log.info("후원 취소 시작 - fundingId: {}, userId: {}", command.fundingId(), command.userId());

		Funding funding = fundingRepository.findById(command.fundingId())
			.orElseThrow(() -> new FundingException(FUNDING_NOT_FOUND));

		if (!funding.getUserId().equals(command.userId())) {
			log.warn("권한 없는 취소 시도 - fundingId: {}, requestUserId: {}, actualUserId: {}",
				command.fundingId(), command.userId(), funding.getUserId());
			throw new FundingException(UNAUTHORIZED_CANCEL);
		}

		if (funding.getStatus() == FundingStatus.CANCELLED) {
			log.warn("이미 취소된 펀딩 - fundingId: {}", command.fundingId());
			throw new FundingException(ALREADY_CANCELLED);
		}

		if (funding.getStatus() != FundingStatus.COMPLETED) {
			log.warn("취소 불가능한 상태 - fundingId: {}, status: {}",
				command.fundingId(), funding.getStatus());
			throw new FundingException(CANNOT_CANCEL_NON_COMPLETED);
		}

		try {
			ProcessRefundRequest refundRequest = ProcessRefundRequest.of(funding.getPaymentId(), command.reason());
			ProcessRefundResponse refundResponse = paymentClient.processRefund(refundRequest);
			log.info("환불 성공 - paymentId: {}", refundResponse.paymentId());

			funding.cancelFunding();
			fundingRepository.save(funding);
			log.info("펀딩 상태 변경 완료 - fundingId: {}, status: CANCELLED", funding.getId());

			if (funding.hasReservation()) {
				Outbox event = Outbox.createOutbox(
					"FUNDING",
					funding.getId(),
					"FUNDING_CANCELLED",
					Map.of(
						"reservationId", funding.getReservationId(),
						"fundingId", funding.getId(),
						"projectId", funding.getProjectId(),
						"userId", funding.getUserId()
					)
				);

				Outbox savedOutbox = outboxRepository.save(event);
				eventPublisher.publishEvent(new OutboxEventCreated(savedOutbox.getId()));
			}

			fundingProjectStatisticsService.decreaseFundingStatusRate(funding.getProjectId(), funding.getAmount());

			return CancelFundingResult.success(funding.getId());
		} catch (FundingException e) {
			log.error("후원 취소 실패 - fundingId: {}, error: {}", command.fundingId(), e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("후원 취소 중 예상치 못한 오류 - fundingId: {}", command.fundingId(), e);
			throw new FundingException(REFUND_FAILED);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public GetMyFundingsResult getMyFundings(GetMyFundingsCommand command) {
		log.info("내 후원 내역 조회 - userId: {}, status: {}, period: {}, sortBy: {}",
			command.userId(), command.status(), command.period(), command.sortBy());

		LocalDateTime startDate = calculateStartDate(command.period());

		Pageable pageable = createPageable(command);

		Page<Funding> fundingPage = fundingRepository.findMyFundings(
			command.userId(),
			command.status(),
			startDate,
			pageable
		);

		log.info("내 후원 내역 조회 완료 - totalElements: {}", fundingPage.getTotalElements());

		return GetMyFundingsResult.of(
			fundingPage.getContent(),
			fundingPage.getTotalElements(),
			command.page(),
			command.size()
		);
	}

	private LocalDateTime calculateStartDate(GetMyFundingsCommand.FundingPeriod period) {
		if (period == null || period == GetMyFundingsCommand.FundingPeriod.ALL) {
			return null;
		}

		LocalDateTime now = LocalDateTime.now();

		return switch (period) {
			case ONE_MONTH -> now.minusMonths(1);
			case THREE_MONTHS -> now.minusMonths(3);
			case ONE_YEAR -> now.minusYears(1);
			case ALL -> null;
		};
	}

	private Pageable createPageable(GetMyFundingsCommand command) {
		Sort sort = switch (command.sortBy()) {
			case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
			case AMOUNT_DESC -> Sort.by(Sort.Direction.DESC, "amount");
			case AMOUNT_ASC -> Sort.by(Sort.Direction.ASC, "amount");
		};

		return PageRequest.of(command.page(), command.size(), sort);
	}

	private void handleFundingFailure(Funding funding, UUID reservationId, String errorMessage) {
		funding.failFunding(errorMessage);

		if (reservationId != null) {
			Outbox event = Outbox.createOutbox(
				"FUNDING",
				funding.getId(),
				"FUNDING_FAILED",
				Map.of(
					"reservationId", reservationId,
					"fundingId", funding.getId()
				)
			);

			Outbox savedOutbox = outboxRepository.save(event);
			eventPublisher.publishEvent(new OutboxEventCreated(savedOutbox.getId()));
		}
	}
}
