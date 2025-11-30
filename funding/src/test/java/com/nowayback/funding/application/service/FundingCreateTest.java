package com.nowayback.funding.application.service;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.nowayback.funding.application.client.payment.PaymentClient;
import com.nowayback.funding.application.client.payment.dto.response.ProcessPaymentResponse;
import com.nowayback.funding.application.client.project.ProjectClient;
import com.nowayback.funding.application.client.reward.RewardClient;
import com.nowayback.funding.application.client.reward.dto.response.DecreaseRewardResponse;
import com.nowayback.funding.application.client.reward.dto.response.RewardDetailResponse;
import com.nowayback.funding.application.funding.dto.command.CreateFundingCommand;
import com.nowayback.funding.application.funding.dto.result.CreateFundingResult;
import com.nowayback.funding.application.funding.service.FundingServiceImpl;
import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.domain.funding.entity.FundingStatus;
import com.nowayback.funding.domain.funding.repository.FundingRepository;
import com.nowayback.funding.domain.funding.repository.OutboxRepository;
import com.nowayback.funding.domain.fundingProjectStatistics.sevice.FundingProjectStatisticsService;

@ExtendWith(MockitoExtension.class)
@DisplayName("FundingService 테스트")
class FundingCreateTest {

	@InjectMocks
	private FundingServiceImpl fundingService;

	@Mock
	private FundingRepository fundingRepository;

	@Mock
	private FundingProjectStatisticsService fundingProjectStatisticsService;

	@Mock
	private RewardClient rewardClient;

	@Mock
	private PaymentClient paymentClient;

	@Mock
	private ProjectClient projectClient;

	@Mock
	private OutboxRepository outboxRepository;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	private UUID userId;
	private UUID projectId;
	private UUID rewardId;
	private UUID optionId;
	private UUID paymentId;
	private UUID reservationId;
	private String idempotencyKey;

	@BeforeEach
	void setUp() {
		userId = UUID.randomUUID();
		projectId = UUID.randomUUID();
		rewardId = UUID.randomUUID();
		optionId = UUID.randomUUID();
		paymentId = UUID.randomUUID();
		reservationId = UUID.randomUUID();
		idempotencyKey = UUID.randomUUID().toString();
	}

	@Test
	@DisplayName("후원 생성 성공 - 리워드 포함")
	void createFunding_WithReward_Success() {
		// given
		CreateFundingCommand.RewardItem rewardItem = new CreateFundingCommand.RewardItem(
			rewardId, optionId, 1
		);

		CreateFundingCommand command = new CreateFundingCommand(
			projectId,
			userId,
			List.of(rewardItem),
			5000L,
			"test_payment_key",
			"test_order_id",
			"CARD",
			idempotencyKey
		);

		RewardDetailResponse rewardDetail = new RewardDetailResponse(
			rewardId, 10000L, optionId, 100
		);

		DecreaseRewardResponse decreaseResponse = new DecreaseRewardResponse(reservationId);
		ProcessPaymentResponse paymentResponse = new ProcessPaymentResponse(paymentId);

		UUID fundingId = UUID.randomUUID();

		given(fundingRepository.findByIdempotencyKey(idempotencyKey))
			.willReturn(Optional.empty());
		given(fundingRepository.existsByUserIdAndProjectIdAndStatus(userId, projectId, FundingStatus.COMPLETED))
			.willReturn(false);
		given(rewardClient.getRewardDetails(any()))
			.willReturn(List.of(rewardDetail));
		given(fundingRepository.save(any(Funding.class)))
			.willAnswer(invocation -> {
				Funding savedFunding = invocation.getArgument(0);
				// Reflection으로 ID 설정 (JPA가 하는 것처럼)
				java.lang.reflect.Field idField = Funding.class.getDeclaredField("id");
				idField.setAccessible(true);
				idField.set(savedFunding, fundingId);
				return savedFunding;
			});
		given(rewardClient.decreaseReward(any()))
			.willReturn(decreaseResponse);
		given(paymentClient.processPayment(any()))
			.willReturn(paymentResponse);

		// when
		CreateFundingResult result = fundingService.createFunding(command);

		// then
		assertThat(result.status()).isEqualTo("SUCCESS");
		assertThat(result.fundingId()).isEqualTo(fundingId);
		verify(fundingProjectStatisticsService).increaseFundingStatusRate(eq(projectId), eq(15000L));
	}

	@Test
	@DisplayName("후원 생성 성공 - 순수 후원")
	void createFunding_WithoutReward_Success() {
		// given
		CreateFundingCommand command = new CreateFundingCommand(
			projectId,
			userId,
			null,
			10000L,
			"test_payment_key",
			"test_order_id",
			"CARD",
			idempotencyKey
		);

		ProcessPaymentResponse paymentResponse = new ProcessPaymentResponse(paymentId);
		UUID fundingId = UUID.randomUUID();

		given(fundingRepository.findByIdempotencyKey(idempotencyKey))
			.willReturn(Optional.empty());
		given(fundingRepository.existsByUserIdAndProjectIdAndStatus(userId, projectId, FundingStatus.COMPLETED))
			.willReturn(false);
		given(fundingRepository.save(any(Funding.class)))
			.willAnswer(invocation -> {
				Funding savedFunding = invocation.getArgument(0);
				java.lang.reflect.Field idField = Funding.class.getDeclaredField("id");
				idField.setAccessible(true);
				idField.set(savedFunding, fundingId);
				return savedFunding;
			});
		given(paymentClient.processPayment(any()))
			.willReturn(paymentResponse);

		// when
		CreateFundingResult result = fundingService.createFunding(command);

		// then
		assertThat(result.status()).isEqualTo("SUCCESS");
		assertThat(result.fundingId()).isEqualTo(fundingId);
		verify(rewardClient, never()).decreaseReward(any());
		verify(fundingProjectStatisticsService).increaseFundingStatusRate(eq(projectId), eq(10000L));
	}

	@Test
	@DisplayName("후원 생성 실패 - 중복 요청 (Idempotency Key)")
	void createFunding_DuplicateIdempotencyKey_ThrowsException() {
		// given
		CreateFundingCommand command = new CreateFundingCommand(
			projectId,
			userId,
			null,
			10000L,
			"test_payment_key",
			"test_order_id",
			"CARD",
			idempotencyKey
		);

		Funding existingFunding = Funding.createFunding(projectId, userId, idempotencyKey, 10000L);

		given(fundingRepository.findByIdempotencyKey(idempotencyKey))
			.willReturn(Optional.of(existingFunding));

		// when & then
		assertThatThrownBy(() -> fundingService.createFunding(command))
			.isInstanceOf(FundingException.class)
			.hasMessageContaining(DUPLICATE_REQUEST.getMessage());

		verify(fundingRepository, never()).save(any());
	}

	@Test
	@DisplayName("후원 생성 실패 - 중복 후원")
	void createFunding_DuplicateFunding_ThrowsException() {
		// given
		CreateFundingCommand command = new CreateFundingCommand(
			projectId,
			userId,
			null,
			10000L,
			"test_payment_key",
			"test_order_id",
			"CARD",
			idempotencyKey
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

	@Test
	@DisplayName("후원 생성 실패 - 리워드 없음")
	void createFunding_RewardNotFound_ThrowsException() {
		// given
		CreateFundingCommand.RewardItem rewardItem = new CreateFundingCommand.RewardItem(
			rewardId, optionId, 1
		);

		CreateFundingCommand command = new CreateFundingCommand(
			projectId,
			userId,
			List.of(rewardItem),
			5000L,
			"test_payment_key",
			"test_order_id",
			"CARD",
			idempotencyKey
		);

		given(fundingRepository.findByIdempotencyKey(idempotencyKey))
			.willReturn(Optional.empty());
		given(fundingRepository.existsByUserIdAndProjectIdAndStatus(userId, projectId, FundingStatus.COMPLETED))
			.willReturn(false);
		given(rewardClient.getRewardDetails(any()))
			.willReturn(List.of()); // 빈 리스트 반환

		// when
		CreateFundingResult result = fundingService.createFunding(command);

		// then
		assertThat(result.status()).isEqualTo("FAILURE");
		assertThat(result.message()).contains("리워드");
	}
}
