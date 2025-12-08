package com.nowayback.funding.service.funding;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nowayback.funding.application.client.payment.PaymentClient;
import com.nowayback.funding.application.client.payment.dto.response.ProcessPaymentResponse;
import com.nowayback.funding.application.client.reward.RewardClient;
import com.nowayback.funding.application.client.reward.dto.response.StockReserveResponse;
import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;
import com.nowayback.funding.application.funding.dto.result.CreateFundingResult;
import com.nowayback.funding.application.funding.service.FundingServiceImpl;
import com.nowayback.funding.application.fundingProjectStatistics.service.FundingProjectStatisticsService;
import com.nowayback.funding.application.outbox.service.OutboxService;
import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.domain.funding.entity.FundingStatus;
import com.nowayback.funding.domain.funding.repository.FundingRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("FundingService - 펀딩 생성 테스트")
class FundingCreateTest {

	@InjectMocks
	private FundingServiceImpl fundingService;

	@Mock
	private FundingRepository fundingRepository;

	@Mock
	private RewardClient rewardClient;

	@Mock
	private PaymentClient paymentClient;

	@Mock
	private FundingProjectStatisticsService fundingProjectStatisticsService;

	@Mock
	private OutboxService outboxService;

	private UUID projectId;
	private UUID userId;
	private UUID rewardId1;
	private UUID rewardId2;
	private UUID optionId1;
	private UUID paymentId;
	private String idempotencyKey;

	@BeforeEach
	void setUp() {
		projectId = UUID.randomUUID();
		userId = UUID.randomUUID();
		rewardId1 = UUID.randomUUID();
		rewardId2 = UUID.randomUUID();
		optionId1 = UUID.randomUUID();
		paymentId = UUID.randomUUID();
		idempotencyKey = UUID.randomUUID().toString();
	}

	@Nested
	@DisplayName("리워드 없는 순수 후원")
	class PureDonation {

		@Test
		@DisplayName("성공 - 리워드 없이 순수 후원만")
		void createFunding_PureDonation_Success() {
			// given
			CreateFundingCommand command = new CreateFundingCommand(
				projectId,
				userId,
				List.of(),
				10000L,
				"payment_key",
				"order_id",
				"CARD",
				idempotencyKey
			);

			given(fundingRepository.findByIdempotencyKey(idempotencyKey))
				.willReturn(Optional.empty());
			given(fundingRepository.existsByUserIdAndProjectIdAndStatus(userId, projectId, FundingStatus.COMPLETED))
				.willReturn(false);

			given(fundingRepository.save(any(Funding.class)))
				.willAnswer(invocation -> {
					Funding funding = invocation.getArgument(0);
					setFundingId(funding, UUID.randomUUID());
					return funding;
				});

			// Mock: Payment 성공 (userId 헤더 포함)
			given(paymentClient.processPayment(eq(userId), any()))
				.willReturn(new ProcessPaymentResponse(paymentId));

			// when
			CreateFundingResult result = fundingService.createFunding(command);

			// then
			assertThat(result.status()).isEqualTo("SUCCESS");
			assertThat(result.fundingId()).isNotNull();

			ArgumentCaptor<Funding> fundingCaptor = ArgumentCaptor.forClass(Funding.class);
			verify(fundingRepository, times(1)).save(fundingCaptor.capture());

			Funding savedFunding = fundingCaptor.getValue();
			assertThat(savedFunding.getAmount()).isEqualTo(10000L);
			assertThat(savedFunding.getUserId()).isEqualTo(userId);
			assertThat(savedFunding.getProjectId()).isEqualTo(projectId);
			assertThat(savedFunding.getStatus()).isEqualTo(FundingStatus.COMPLETED);
			assertThat(savedFunding.getReservations()).isEmpty();

			// Payment 호출 확인 (userId와 함께)
			verify(paymentClient, times(1)).processPayment(eq(userId), any());
			verify(rewardClient, never()).reserveStock(any());

			verify(fundingProjectStatisticsService, times(1))
				.increaseFundingStatusRate(projectId, 10000L);
		}
	}

	@Nested
	@DisplayName("리워드 포함 후원")
	class RewardDonation {

		@Test
		@DisplayName("성공 - 리워드 1개 + 추가 후원금")
		void createFunding_WithOneReward_Success() {
			// given
			CreateFundingCommand.RewardItem rewardItem = new CreateFundingCommand.RewardItem(
				rewardId1,
				optionId1,
				2
			);

			CreateFundingCommand command = new CreateFundingCommand(
				projectId,
				userId,
				List.of(rewardItem),
				5000L,
				"payment_key",
				"order_id",
				"CARD",
				idempotencyKey
			);

			given(fundingRepository.findByIdempotencyKey(idempotencyKey))
				.willReturn(Optional.empty());
			given(fundingRepository.existsByUserIdAndProjectIdAndStatus(userId, projectId, FundingStatus.COMPLETED))
				.willReturn(false);

			UUID fundingId = UUID.randomUUID();
			given(fundingRepository.save(any(Funding.class)))
				.willAnswer(invocation -> {
					Funding funding = invocation.getArgument(0);
					setFundingId(funding, fundingId);
					return funding;
				});

			UUID reservationId1 = UUID.randomUUID();
			StockReserveResponse.ReservedItem reservedItem = new StockReserveResponse.ReservedItem(
				reservationId1,
				rewardId1,
				optionId1,
				2,
				40000L
			);

			StockReserveResponse stockReserveResponse = new StockReserveResponse(
				fundingId,
				List.of(reservedItem),
				40000L
			);

			given(rewardClient.reserveStock(any()))
				.willReturn(stockReserveResponse);

			// Mock: Payment 성공 (userId 헤더 포함)
			given(paymentClient.processPayment(eq(userId), any()))
				.willReturn(new ProcessPaymentResponse(paymentId));

			// when
			CreateFundingResult result = fundingService.createFunding(command);

			// then
			assertThat(result.status()).isEqualTo("SUCCESS");
			assertThat(result.fundingId()).isNotNull();

			ArgumentCaptor<Funding> fundingCaptor = ArgumentCaptor.forClass(Funding.class);
			verify(fundingRepository, times(1)).save(fundingCaptor.capture());

			Funding savedFunding = fundingCaptor.getValue();
			assertThat(savedFunding.getAmount()).isEqualTo(45000L);
			assertThat(savedFunding.getReservations()).hasSize(1);

			assertThat(savedFunding.getReservations().get(0).getReservationId()).isEqualTo(reservationId1);
			assertThat(savedFunding.getReservations().get(0).getRewardId()).isEqualTo(rewardId1);
			assertThat(savedFunding.getReservations().get(0).getOptionId()).isEqualTo(optionId1);
			assertThat(savedFunding.getReservations().get(0).getQuantity()).isEqualTo(2);
			assertThat(savedFunding.getReservations().get(0).getAmount()).isEqualTo(40000L);

			verify(rewardClient, times(1)).reserveStock(any());
			verify(paymentClient, times(1)).processPayment(eq(userId), any());

			verify(fundingProjectStatisticsService, times(1))
				.increaseFundingStatusRate(projectId, 45000L);

			verify(outboxService, times(1)).publishSuccessEvent(
				eq("FUNDING"),
				eq(savedFunding.getId()),
				eq("FUNDING_COMPLETED"),
				any()
			);
		}

		@Test
		@DisplayName("성공 - 리워드 여러 개 + 추가 후원금")
		void createFunding_WithMultipleRewards_Success() {
			// given
			CreateFundingCommand.RewardItem item1 = new CreateFundingCommand.RewardItem(
				rewardId1,
				optionId1,
				2
			);
			CreateFundingCommand.RewardItem item2 = new CreateFundingCommand.RewardItem(
				rewardId2,
				null,
				1
			);

			CreateFundingCommand command = new CreateFundingCommand(
				projectId,
				userId,
				List.of(item1, item2),
				5000L,
				"payment_key",
				"order_id",
				"CARD",
				idempotencyKey
			);

			given(fundingRepository.findByIdempotencyKey(idempotencyKey))
				.willReturn(Optional.empty());
			given(fundingRepository.existsByUserIdAndProjectIdAndStatus(userId, projectId, FundingStatus.COMPLETED))
				.willReturn(false);

			UUID fundingId = UUID.randomUUID();
			given(fundingRepository.save(any(Funding.class)))
				.willAnswer(invocation -> {
					Funding funding = invocation.getArgument(0);
					setFundingId(funding, fundingId);
					return funding;
				});

			UUID resId1 = UUID.randomUUID();
			UUID resId2 = UUID.randomUUID();

			StockReserveResponse.ReservedItem reserved1 = new StockReserveResponse.ReservedItem(
				resId1, rewardId1, optionId1, 2, 40000L
			);
			StockReserveResponse.ReservedItem reserved2 = new StockReserveResponse.ReservedItem(
				resId2, rewardId2, null, 1, 30000L
			);

			StockReserveResponse response = new StockReserveResponse(
				fundingId,
				List.of(reserved1, reserved2),
				70000L
			);

			given(rewardClient.reserveStock(any())).willReturn(response);
			given(paymentClient.processPayment(eq(userId), any())).willReturn(new ProcessPaymentResponse(paymentId));

			// when
			CreateFundingResult result = fundingService.createFunding(command);

			// then
			assertThat(result.status()).isEqualTo("SUCCESS");

			ArgumentCaptor<Funding> captor = ArgumentCaptor.forClass(Funding.class);
			verify(fundingRepository).save(captor.capture());

			Funding savedFunding = captor.getValue();
			assertThat(savedFunding.getAmount()).isEqualTo(75000L);
			assertThat(savedFunding.getReservations()).hasSize(2);

			assertThat(savedFunding.getReservations().get(0).getReservationId()).isEqualTo(resId1);
			assertThat(savedFunding.getReservations().get(0).getAmount()).isEqualTo(40000L);

			assertThat(savedFunding.getReservations().get(1).getReservationId()).isEqualTo(resId2);
			assertThat(savedFunding.getReservations().get(1).getAmount()).isEqualTo(30000L);

			List<UUID> reservationIds = savedFunding.getReservationIds();
			assertThat(reservationIds).hasSize(2);
			assertThat(reservationIds).containsExactly(resId1, resId2);
		}
	}

	@Nested
	@DisplayName("검증 실패 케이스")
	class ValidationFailures {

		@Test
		@DisplayName("실패 - 멱등성 키 중복")
		void createFunding_DuplicateIdempotencyKey_Failure() {
			// given
			CreateFundingCommand command = new CreateFundingCommand(
				projectId, userId, List.of(), 10000L,
				"payment_key", "order_id", "CARD", idempotencyKey
			);

			Funding existingFunding = Funding.createFunding(projectId, userId, idempotencyKey, 10000L);
			given(fundingRepository.findByIdempotencyKey(idempotencyKey))
				.willReturn(Optional.of(existingFunding));

			// when & then
			assertThatThrownBy(() -> fundingService.createFunding(command))
				.isInstanceOf(FundingException.class)
				.hasMessageContaining(DUPLICATE_REQUEST.getMessage());

			verify(fundingRepository, never()).save(any());
			verify(rewardClient, never()).reserveStock(any());
			verify(paymentClient, never()).processPayment(any(), any());
		}

		@Test
		@DisplayName("실패 - 중복 후원")
		void createFunding_DuplicateFunding_Failure() {
			// given
			CreateFundingCommand command = new CreateFundingCommand(
				projectId, userId, List.of(), 10000L,
				"payment_key", "order_id", "CARD", idempotencyKey
			);

			given(fundingRepository.findByIdempotencyKey(idempotencyKey))
				.willReturn(Optional.empty());
			given(fundingRepository.existsByUserIdAndProjectIdAndStatus(userId, projectId, FundingStatus.COMPLETED))
				.willReturn(true);

			// when & then
			assertThatThrownBy(() -> fundingService.createFunding(command))
				.isInstanceOf(FundingException.class)
				.hasMessageContaining(DUPLICATE_FUNDING.getMessage());

			verify(fundingRepository, never()).save(any());
		}
	}

	@Nested
	@DisplayName("보상 트랜잭션")
	class CompensationTransaction {

		@Test
		@DisplayName("Payment 실패 시 Reward 예약 취소 이벤트 발행")
		void createFunding_PaymentFailed_CompensationEvent() {
			// given
			CreateFundingCommand.RewardItem item = new CreateFundingCommand.RewardItem(
				rewardId1, optionId1, 2
			);

			CreateFundingCommand command = new CreateFundingCommand(
				projectId, userId, List.of(item), 5000L,
				"payment_key", "order_id", "CARD", idempotencyKey
			);

			given(fundingRepository.findByIdempotencyKey(idempotencyKey))
				.willReturn(Optional.empty());
			given(fundingRepository.existsByUserIdAndProjectIdAndStatus(userId, projectId, FundingStatus.COMPLETED))
				.willReturn(false);

			UUID fundingId = UUID.randomUUID();
			given(fundingRepository.save(any(Funding.class)))
				.willAnswer(invocation -> {
					Funding funding = invocation.getArgument(0);
					setFundingId(funding, fundingId);
					return funding;
				});

			UUID resId = UUID.randomUUID();
			StockReserveResponse response = new StockReserveResponse(
				fundingId,
				List.of(new StockReserveResponse.ReservedItem(resId, rewardId1, optionId1, 2, 40000L)),
				40000L
			);
			given(rewardClient.reserveStock(any())).willReturn(response);

			// Payment 실패! (userId 파라미터 포함)
			given(paymentClient.processPayment(eq(userId), any()))
				.willThrow(new RuntimeException("Payment failed"));

			// when
			CreateFundingResult result = fundingService.createFunding(command);

			// then
			assertThat(result.status()).isEqualTo("FAILURE");
			assertThat(result.message()).contains("후원 처리 중 오류가 발생했습니다");

			verify(outboxService, times(1)).publishCompensationEvent(
				eq("FUNDING"),
				eq(fundingId),
				eq("FUNDING_FAILED"),
				argThat(payload -> {
					List<UUID> reservationIds = (List<UUID>) payload.get("reservationIds");
					return reservationIds != null && reservationIds.contains(resId);
				})
			);
		}
	}

	// Helper: Reflection으로 ID 주입
	private void setFundingId(Funding funding, UUID id) {
		try {
			java.lang.reflect.Field idField = Funding.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(funding, id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}