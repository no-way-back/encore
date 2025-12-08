package com.nowayback.funding.application.funding.service;

import static com.nowayback.funding.domain.event.FundingProducerTopics.*;
import static com.nowayback.funding.domain.exception.FundingErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.nowayback.funding.application.client.payment.dto.response.ProcessPaymentResponse;
import com.nowayback.funding.application.client.reward.dto.request.StockReserveRequest;
import com.nowayback.funding.application.client.reward.dto.response.StockReserveResponse;
import com.nowayback.funding.application.funding.dto.command.CancelFundingCommand;
import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;
import com.nowayback.funding.application.funding.dto.command.GetMyFundingsCommand;
import com.nowayback.funding.application.funding.dto.command.GetProjectSponsorsCommand;
import com.nowayback.funding.application.funding.dto.result.CancelFundingResult;
import com.nowayback.funding.application.funding.dto.result.CreateFundingResult;
import com.nowayback.funding.application.funding.dto.result.FundingDetailResult;
import com.nowayback.funding.application.funding.dto.result.GetMyFundingsResult;
import com.nowayback.funding.application.funding.dto.result.GetProjectSponsorsResult;
import com.nowayback.funding.application.outbox.service.OutboxService;
import com.nowayback.funding.application.fundingProjectStatistics.service.FundingProjectStatisticsService;
import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.domain.funding.entity.FundingStatus;
import com.nowayback.funding.domain.funding.repository.FundingRepository;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FundingServiceImpl implements FundingService {

	private final FundingRepository fundingRepository;
	private final FundingProjectStatisticsService fundingProjectStatisticsService;
	private final RewardClient rewardClient;
	private final PaymentClient paymentClient;
	private final OutboxService outboxService;

	public FundingServiceImpl(FundingRepository fundingRepository,
		FundingProjectStatisticsService fundingProjectStatisticsService, RewardClient rewardClient,
		PaymentClient paymentClient, OutboxService outboxService) {
		this.fundingRepository = fundingRepository;
		this.fundingProjectStatisticsService = fundingProjectStatisticsService;
		this.rewardClient = rewardClient;
		this.paymentClient = paymentClient;
		this.outboxService = outboxService;
	}

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

		fundingProjectStatisticsService.validateProjectForFunding(command.projectId());

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

		Funding funding = Funding.createFunding(
			command.projectId(),
			command.userId(),
			command.idempotencyKey(),
			command.amount()
		);

		Funding savedFunding = fundingRepository.save(funding);
		log.info("Funding 생성 완료 - funding: {}", savedFunding.getId());

		if (command.hasRewards()) {
			List<StockReserveRequest.StockReserveItem> items = command.rewardItems().stream()
				.map(item -> new StockReserveRequest.StockReserveItem(
					item.rewardId(),
					item.optionId(),
					item.quantity()
				))
				.toList();

			StockReserveRequest stockReserveRequest = new StockReserveRequest(savedFunding.getId(), items);
			StockReserveResponse response = rewardClient.reserveStock(command.userId(), stockReserveRequest);

			log.info("재고 예약 완료 - fundingId: {}, reservations: {}, rewardAmount: {}, totalAmount: {}",
				savedFunding.getId(), response.reservedItems().size(), response.totalAmount(), response.totalAmount());

			for (StockReserveResponse.ReservedItem item : response.reservedItems()) {
				funding.addReservation(
					item.reservationId(),
					item.rewardId(),
					item.optionId(),
					item.quantity(),
					item.itemAmount()
				);
			}

			Long totalAmount = response.totalAmount() + command.amount();

			funding.updateAmount(totalAmount);
		}

		try {
			ProcessPaymentRequest paymentRequest = ProcessPaymentRequest.from(funding.getId(), command);
			ProcessPaymentResponse paymentResponse = paymentClient.processPayment(command.userId(), paymentRequest);
			UUID paymentId = paymentResponse.paymentId();
			log.info("결제 성공 - paymentId: {}", paymentId);

			funding.completeFunding(paymentId);
			log.info("펀딩 완료 - funding: {}", funding.getId());

			fundingProjectStatisticsService.increaseFundingStatusRate(funding.getProjectId(), funding.getAmount());

			publishFundingCompletedEvent(funding);

			return CreateFundingResult.success(funding.getId());
		} catch (FundingException e) {
			log.error("펀딩 처리 실패 - fundingId: {}, errorCode: {}, message: {}",
				funding.getId(), e.getErrorCode().getCode(), e.getMessage());

			funding.failFunding(e.getMessage());

			publishFundingFailedEvent(funding, command);

			throw e;

		} catch (FeignException e) {
			log.error("외부 서비스 호출 실패 - fundingId: {}, url: {}, error: {}",
				funding.getId(), e.request().url(), e.getMessage());

			funding.failFunding(e.getMessage());

			publishFundingFailedEvent(funding, command);

			throw e;
		} catch (Exception e) {
			log.error("예상치 못한 오류 - fundingId: {}, error: {}",
				funding.getId(), e.getMessage(), e);

			funding.failFunding(e.getMessage());

			publishFundingFailedEvent(funding, command);

			throw new FundingException(FUNDING_PROCESS_FAILED);
		}
	}

	private void publishFundingCompletedEvent(Funding funding) {
		if (!funding.hasReservation()) {
			return;
		}

		outboxService.publishSuccessEvent(
			"FUNDING",
			funding.getId(),
			FUNDING_COMPLETED,
			Map.of(
				"fundingId", funding.getId(),
				"userId", funding.getUserId(),
				"projectId", funding.getProjectId(),
				"reservationId", funding.getReservationIds(),
				"amount", funding.getAmount()
			)
		);
	}

	private void publishFundingFailedEvent(Funding funding, CreateFundingCommand command) {
		if (!funding.hasReservation()) {
			return;
		}

		outboxService.publishCompensationEvent(
			"FUNDING",
			funding.getId(),
			FUNDING_FAILED,
			Map.of(
				"fundingId", funding.getId(),
				"projectId", command.projectId(),
				"userId", command.userId(),
				"reservationId", funding.getReservationIds()
			)
		);
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

		ProcessRefundRequest refundRequest = ProcessRefundRequest.of(funding.getPaymentId(), command.reason());
		ProcessRefundResponse refundResponse = paymentClient.processRefund(refundRequest);
		log.info("환불 성공 - paymentId: {}", refundResponse.paymentId());

		funding.cancelFunding();
		fundingRepository.save(funding);
		log.info("펀딩 상태 변경 완료 - fundingId: {}, status: CANCELLED", funding.getId());

		fundingProjectStatisticsService.decreaseFundingStatusRate(funding.getProjectId(), funding.getAmount());

		publishFundingRefundEvent(funding);

		return CancelFundingResult.success(funding.getId());
	}

	private void publishFundingRefundEvent(Funding funding) {
		if (!funding.hasReservation()) {
			return;
		}

		outboxService.publishSuccessEvent(
			"FUNDING",
			funding.getId(),
			FUNDING_REFUND,
			Map.of(
				"fundingId", funding.getId(),
				"projectId", funding.getProjectId(),
				"userId", funding.getUserId(),
				"reservationId", funding.getReservationIds()
			)
		);
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

	@Override
	@Transactional(readOnly = true)
	public GetProjectSponsorsResult getProjectSponsors(GetProjectSponsorsCommand command) {
		log.info("프로젝트 후원자 조회 - projectId: {}, creatorId: {}",
			command.projectId(), command.creatorId());

		fundingProjectStatisticsService.validateProjectCreator(
			command.projectId(),
			command.creatorId()
		);

		Pageable pageable = PageRequest.of(command.page(), command.size());

		Page<Funding> fundingPage = fundingRepository.findProjectSponsors(
			command.projectId(),
			FundingStatus.COMPLETED,
			pageable
		);

		Long totalAmount = fundingRepository.sumAmountByProjectIdAndStatus(
			command.projectId(),
			FundingStatus.COMPLETED
		);

		log.info("프로젝트 후원자 조회 완료 - projectId: {}, totalSponsors: {}, totalAmount: {}",
			command.projectId(), fundingPage.getTotalElements(), totalAmount);

		return GetProjectSponsorsResult.of(
			command.projectId(),
			fundingPage.getContent(),
			fundingPage.getTotalElements(),
			totalAmount,
			command.page(),
			command.size()
		);
	}

	@Override
	public FundingDetailResult getFundingDetail(UUID fundingId) {
		Funding funding = fundingRepository.findById(fundingId)
			.orElseThrow(() -> new FundingException(FUNDING_NOT_FOUND));

		return FundingDetailResult.from(funding);
	}

	private LocalDateTime calculateStartDate(GetMyFundingsCommand.FundingPeriod period) {

		LocalDateTime now = LocalDateTime.now();

		return switch (period) {
			case ALL -> null;
			case null -> null;
			case ONE_MONTH -> now.minusMonths(1);
			case THREE_MONTHS -> now.minusMonths(3);
			case ONE_YEAR -> now.minusYears(1);
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
}
