package com.nowayback.funding.application.service;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

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
import com.nowayback.funding.application.client.payment.dto.response.ProcessRefundResponse;
import com.nowayback.funding.application.client.project.ProjectClient;
import com.nowayback.funding.application.client.reward.RewardClient;
import com.nowayback.funding.application.funding.dto.command.CancelFundingCommand;
import com.nowayback.funding.application.funding.dto.result.CancelFundingResult;
import com.nowayback.funding.application.funding.service.FundingServiceImpl;
import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.domain.funding.entity.FundingStatus;
import com.nowayback.funding.domain.funding.repository.FundingRepository;
import com.nowayback.funding.domain.outbox.repository.OutboxRepository;
import com.nowayback.funding.application.fundingProjectStatistics.service.FundingProjectStatisticsService;

@ExtendWith(MockitoExtension.class)
@DisplayName("FundingService 취소 테스트")
class FundingCancelTest {

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
	private UUID fundingId;
	private UUID projectId;
	private UUID paymentId;
	private UUID reservationId;

	@BeforeEach
	void setUp() {
		userId = UUID.randomUUID();
		fundingId = UUID.randomUUID();
		projectId = UUID.randomUUID();
		paymentId = UUID.randomUUID();
		reservationId = UUID.randomUUID();
	}

	@Test
	@DisplayName("후원 취소 성공 - 리워드 포함")
	void cancelFunding_WithReward_Success() throws Exception {
		// given
		CancelFundingCommand command = new CancelFundingCommand(
			fundingId,
			userId,
			"단순 변심"
		);

		Funding funding = Funding.createFunding(projectId, userId, "test-key", 15000L);

		// Reflection으로 필드 설정 (Mock JPA 동작)
		setField(funding, "id", fundingId);
		funding.completeFunding(reservationId, paymentId);

		ProcessRefundResponse refundResponse = new ProcessRefundResponse(paymentId);

		given(fundingRepository.findById(fundingId))
			.willReturn(Optional.of(funding));
		given(paymentClient.processRefund(any()))
			.willReturn(refundResponse);
		given(outboxRepository.save(any()))
			.willAnswer(invocation -> invocation.getArgument(0));

		// when
		CancelFundingResult result = fundingService.cancelFunding(command);

		// then
		assertThat(result.status()).isEqualTo("SUCCESS");
		assertThat(result.fundingId()).isEqualTo(fundingId);
		assertThat(funding.getStatus()).isEqualTo(FundingStatus.CANCELLED);
		verify(fundingProjectStatisticsService).decreaseFundingStatusRate(eq(projectId), eq(15000L));
		verify(outboxRepository).save(any());
	}

	@Test
	@DisplayName("후원 취소 성공 - 순수 후원")
	void cancelFunding_WithoutReward_Success() throws Exception {
		// given
		CancelFundingCommand command = new CancelFundingCommand(
			fundingId,
			userId,
			"단순 변심"
		);

		Funding funding = Funding.createFunding(projectId, userId, "test-key", 10000L);

		// Reflection으로 필드 설정 (Mock JPA 동작)
		setField(funding, "id", fundingId);
		funding.completeFunding(null, paymentId);

		ProcessRefundResponse refundResponse = new ProcessRefundResponse(paymentId);

		given(fundingRepository.findById(fundingId))
			.willReturn(Optional.of(funding));
		given(paymentClient.processRefund(any()))
			.willReturn(refundResponse);

		// when
		CancelFundingResult result = fundingService.cancelFunding(command);

		// then
		assertThat(result.status()).isEqualTo("SUCCESS");
		verify(outboxRepository, never()).save(any());
		verify(fundingProjectStatisticsService).decreaseFundingStatusRate(eq(projectId), eq(10000L));
	}

	// 헬퍼 메서드
	private void setField(Object target, String fieldName, Object value) throws Exception {
		java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}

	@Test
	@DisplayName("후원 취소 실패 - 존재하지 않는 펀딩")
	void cancelFunding_FundingNotFound_ThrowsException() {
		// given
		CancelFundingCommand command = new CancelFundingCommand(
			fundingId,
			userId,
			"단순 변심"
		);

		given(fundingRepository.findById(fundingId))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> fundingService.cancelFunding(command))
			.isInstanceOf(FundingException.class)
			.hasMessageContaining(FUNDING_NOT_FOUND.getMessage());

		verify(paymentClient, never()).processRefund(any());
	}

	@Test
	@DisplayName("후원 취소 실패 - 권한 없음")
	void cancelFunding_Unauthorized_ThrowsException() throws Exception {
		// given
		UUID differentUserId = UUID.randomUUID();

		CancelFundingCommand command = new CancelFundingCommand(
			fundingId,
			differentUserId,
			"단순 변심"
		);

		Funding funding = Funding.createFunding(projectId, userId, "test-key", 15000L);
		setField(funding, "id", fundingId);

		given(fundingRepository.findById(fundingId))
			.willReturn(Optional.of(funding));

		// when & then
		assertThatThrownBy(() -> fundingService.cancelFunding(command))
			.isInstanceOf(FundingException.class)
			.hasMessageContaining(UNAUTHORIZED_CANCEL.getMessage());

		verify(paymentClient, never()).processRefund(any());
	}

	@Test
	@DisplayName("후원 취소 실패 - 이미 취소됨")
	void cancelFunding_AlreadyCancelled_ThrowsException() throws Exception {
		// given
		CancelFundingCommand command = new CancelFundingCommand(
			fundingId,
			userId,
			"단순 변심"
		);

		Funding funding = Funding.createFunding(projectId, userId, "test-key", 15000L);
		setField(funding, "id", fundingId);
		funding.completeFunding(reservationId, paymentId);
		funding.cancelFunding();

		given(fundingRepository.findById(fundingId))
			.willReturn(Optional.of(funding));

		// when & then
		assertThatThrownBy(() -> fundingService.cancelFunding(command))
			.isInstanceOf(FundingException.class)
			.hasMessageContaining(ALREADY_CANCELLED.getMessage());

		verify(paymentClient, never()).processRefund(any());
	}

	@Test
	@DisplayName("후원 취소 실패 - COMPLETED 상태가 아님")
	void cancelFunding_NotCompleted_ThrowsException() throws Exception {
		// given
		CancelFundingCommand command = new CancelFundingCommand(
			fundingId,
			userId,
			"단순 변심"
		);

		Funding funding = Funding.createFunding(projectId, userId, "test-key", 15000L);
		setField(funding, "id", fundingId);
		// PENDING 상태 (completeFunding 호출 안 함)

		given(fundingRepository.findById(fundingId))
			.willReturn(Optional.of(funding));

		// when & then
		assertThatThrownBy(() -> fundingService.cancelFunding(command))
			.isInstanceOf(FundingException.class)
			.hasMessageContaining(CANNOT_CANCEL_NON_COMPLETED.getMessage());

		verify(paymentClient, never()).processRefund(any());
	}
}