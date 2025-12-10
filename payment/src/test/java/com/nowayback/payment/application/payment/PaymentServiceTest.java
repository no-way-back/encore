package com.nowayback.payment.application.payment;

import com.nowayback.payment.application.payment.dto.result.PaymentResult;
import com.nowayback.payment.application.payment.service.pg.PaymentGatewayClient;
import com.nowayback.payment.domain.exception.PaymentErrorCode;
import com.nowayback.payment.domain.exception.PaymentException;
import com.nowayback.payment.domain.payment.repository.PaymentRepository;
import com.nowayback.payment.domain.payment.vo.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

import static com.nowayback.payment.fixture.PaymentFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("결제 서비스")
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentGatewayClient paymentGatewayClient;

    @Mock
    private PaymentStatusLogService paymentStatusLogService;

    @InjectMocks
    private PaymentService paymentService;

    @Nested
    @DisplayName("결제 생성")
    class CreatePayment {

        @Test
        @DisplayName("유효한 정보로 결제를 생성하면 결제 정보가 저장되고 생성된 결제 정보가 반환된다.")
        void createPayment_whenValid_thenCreatePayment() {
            /* given */
            when(paymentRepository.save(any()))
                    .thenReturn(createPaymentWithStatus(PaymentStatus.PENDING));

            /* when */
            PaymentResult result = paymentService.createPayment(CREATE_PAYMENT_COMMAND);

            /* then */
            assertThat(result.status()).isEqualTo(PaymentStatus.PENDING);
            verify(paymentRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("이미 대기중인 결제가 존재하는 경우 결제 생성 시 예외가 발생한다.")
        void createPayment_whenExistingPendingPayment_thenThrowException() {
            /* given */
            when(paymentRepository.existsByFundingIdAndStatus(any(FundingId.class), any()))
                    .thenReturn(true);

            /* when */
            /* then */
            assertThatThrownBy(() -> paymentService.createPayment(CREATE_PAYMENT_COMMAND))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.PAYMENT_ALREADY_PENDING.getMessage());
        }
    }

    @Nested
    @DisplayName("결제 목록 조회")
    class GetPayments {

        @ParameterizedTest(name = "{0} 역할로 결제 목록 조회")
        @DisplayName("MASTER나 ADMIN 역할로 결제 목록을 조회하면 조건에 맞는 결제 목록이 반환된다.")
        @ValueSource(strings = {"MASTER", "ADMIN"})
        void getPayments_whenValid_thenReturnPayments(String role) {
            /* given */
            when(paymentRepository.searchPayments(any(), any(), any()))
                    .thenReturn(PAYMENT_PAGE);

            /* when */
            Page<PaymentResult> results = paymentService.getPayments(
                    USER_UUID,
                    PROJECT_UUID,
                    PAGE,
                    SIZE,
                    UUID.randomUUID(),
                    role
            );

            /* then */
            assertThat(results.getTotalElements()).isEqualTo(PAYMENT_PAGE.getTotalElements());
            verify(paymentRepository, times(1)).searchPayments(any(), any(), any());
        }

        @Test
        @DisplayName("USER 역할로 본인의 결제 목록을 조회하면 조건에 맞는 결제 목록이 반환된다.")
        void getPayments_whenUserSelf_thenReturnPayments() {
            /* given */
            when(paymentRepository.searchPayments(any(), any(), any()))
                    .thenReturn(PAYMENT_PAGE);

            /* when */
            Page<PaymentResult> results = paymentService.getPayments(
                    USER_UUID,
                    PROJECT_UUID,
                    PAGE,
                    SIZE,
                    USER_UUID,
                    "USER"
            );

            /* then */
            assertThat(results.getTotalElements()).isEqualTo(PAYMENT_PAGE.getTotalElements());
            verify(paymentRepository, times(1)).searchPayments(any(), any(), any());
        }

        @Test
        @DisplayName("USER 역할로 다른 사용자의 결제 목록을 조회하면 예외가 발생한다.")
        void getPayments_whenUserNotSelf_thenThrowException() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> paymentService.getPayments(
                    USER_UUID,
                    PROJECT_UUID,
                    PAGE,
                    SIZE,
                    UUID.randomUUID(),
                    "USER"
            ))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.FORBIDDEN_PAYMENT_SELF_ACCESS.getMessage());
        }

        @Test
        @DisplayName("USER 역할로 유저 조건 없이 결제 목록을 조회하면 본인의 결제 목록이 반환된다.")
        void getPayments_whenUserNotSelfAndNoUserId_thenReturnPayments() {
            /* given */
            when(paymentRepository.searchPayments(any(), any(), any()))
                    .thenReturn(PAYMENT_PAGE);

            /* when */
            Page<PaymentResult> results = paymentService.getPayments(
                    null,
                    PROJECT_UUID,
                    PAGE,
                    SIZE,
                    USER_UUID,
                    "USER"
            );

            /* then */
            assertThat(results.getTotalElements()).isEqualTo(PAYMENT_PAGE.getTotalElements());
            verify(paymentRepository, times(1)).searchPayments(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("결제 승인")
    class Confirm {

        @Test
        @DisplayName("유효한 정보로 결제 승인을 하면 결제 승인이 요청되고 결제 정보가 저장된다.")
        void confirm_whenValid_thenCreatePayment() {
            /* given */
            when(paymentRepository.findByFundingIdAndStatus(any(FundingId.class), any()))
                    .thenReturn(Optional.of(createPaymentWithStatus(PaymentStatus.PENDING)));
            when(paymentGatewayClient.confirmPayment(any(PgInfo.class), any(Money.class)))
                    .thenReturn(PG_CONFIRM_RESULT);

            /* when */
            PaymentResult result = paymentService.confirmPayment(CONFIRM_PAYMENT_COMMAND, USER_UUID);

            /* then */
            assertThat(result.status()).isEqualTo(PaymentStatus.COMPLETED);

            verify(paymentGatewayClient, times(1)).confirmPayment(any(PgInfo.class), any(Money.class));
            verify(paymentRepository, times(1)).save(any());
            verify(paymentStatusLogService, times(1)).savePaymentStatusLog(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("대기중인 결제가 없으면 결제 승인 시 예외가 발생한다.")
        void confirm_whenNoPendingPayment_thenThrowException() {
            /* given */
            when(paymentRepository.findByFundingIdAndStatus(any(FundingId.class), any()))
                    .thenReturn(Optional.empty());

            /* when */
            /* then */
            assertThatThrownBy(() -> paymentService.confirmPayment(CONFIRM_PAYMENT_COMMAND, USER_UUID))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.PENDING_PAYMENT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("본인이 아닌 사용자가 결제 승인을 시도하면 예외가 발생한다.")
        void confirm_whenNotSelf_thenThrowException() {
            /* given */
            when(paymentRepository.findByFundingIdAndStatus(any(FundingId.class), any()))
                    .thenReturn(Optional.of(createPaymentWithStatus(PaymentStatus.PENDING)));

            /* when */
            /* then */
            assertThatThrownBy(() -> paymentService.confirmPayment(CONFIRM_PAYMENT_COMMAND, UUID.randomUUID()))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.FORBIDDEN_PAYMENT_SELF_ACCESS.getMessage());
        }
    }

    @Nested
    @DisplayName("결제 환불")
    class Refund {

        @Test
        @DisplayName("유효한 정보로 결제 환불을 하면 결제 환불이 요청되고 결제 정보가 수정된다.")
        void refund_whenValid_thenRefundPayment() {
            /* given */
            when(paymentRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(createConfirmedPayment()));
            when(paymentGatewayClient.refundPayment(anyString(), anyString(), any(RefundAccountInfo.class)))
                    .thenReturn(PG_REFUND_RESULT);

            /* when */
            PaymentResult result = paymentService.refundPayment(REFUND_PAYMENT_COMMAND);

            /* then */
            assertThat(result.status()).isEqualTo(PaymentStatus.REFUNDED);

            verify(paymentGatewayClient, times(1)).refundPayment(anyString(), anyString(), any(RefundAccountInfo.class));
            verify(paymentStatusLogService, times(1)).savePaymentStatusLog(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("이미 환불된 결제에 대해 환불을 시도하면 예외가 발생한다.")
        void refund_whenAlreadyRefunded_thenThrowException() {
            /* given */
            when(paymentRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(createPaymentWithStatus(PaymentStatus.REFUNDED)));

            /* when */
            /* then */
            assertThatThrownBy(() -> paymentService.refundPayment(REFUND_PAYMENT_COMMAND))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.PAYMENT_ALREADY_REFUNDED.getMessage());
        }
    }

    @Nested
    @DisplayName("프로젝트 ID로 총 결제 금액 조회")
    class GetTotalAmountByProjectId {

        @Test
        @DisplayName("프로젝트 ID에 대한 총 결제 금액을 반환한다.")
        void getTotalAmountByProjectId_returnTotalAmount() {
            /* given */
            when(paymentRepository.sumAmountByProjectId(any()))
                    .thenReturn(150000L);

            /* when */
            Long totalAmount = paymentService.getTotalAmountByProjectId(PROJECT_UUID);

            /* then */
            assertThat(totalAmount).isEqualTo(150000L);
        }
    }
}