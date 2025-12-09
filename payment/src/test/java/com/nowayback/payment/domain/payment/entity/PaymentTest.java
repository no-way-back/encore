package com.nowayback.payment.domain.payment.entity;

import com.nowayback.payment.domain.exception.PaymentErrorCode;
import com.nowayback.payment.domain.exception.PaymentException;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.*;
import static com.nowayback.payment.fixture.PaymentFixture.*;

@DisplayName("결제 엔티티")
class PaymentTest {

    @Nested
    @DisplayName("결제 엔티티 생성")
    class Create {

        @Test
        @DisplayName("모든 필드가 유효하면 결제 엔티티 생성에 성공한다.")
        void create_givenValidFields_thenSuccess() {
            /* given */
            /* when */
            Payment payment = createPayment();

            /* then */
            assertThat(payment.getUserId()).isEqualTo(USER_ID);
            assertThat(payment.getFundingId()).isEqualTo(FUNDING_ID);
            assertThat(payment.getAmount()).isEqualTo(AMOUNT);
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(payment.getPgInfo()).isEqualTo(PG_INFO);
            assertThat(payment.getRefundAccountInfo()).isNull();
        }

        @Test
        @DisplayName("null 유저 아이디로 생성 시 예외가 발생한다.")
        void create_givenNullUserId_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> Payment.create(null, FUNDING_ID, PROJECT_ID, AMOUNT, PG_INFO))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_USER_ID_OBJECT.getMessage());
        }

        @Test
        @DisplayName("null 펀딩 아이디로 생성 시 예외가 발생한다.")
        void create_givenNullFundingId_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> Payment.create(USER_ID, null, PROJECT_ID, AMOUNT, PG_INFO))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_FUNDING_ID_OBJECT.getMessage());
        }

        @Test
        @DisplayName("null 프로젝트 아이디로 생성 시 예외가 발생한다.")
        void create_givenNullProjectId_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> Payment.create(USER_ID, FUNDING_ID, null, AMOUNT, PG_INFO))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_PAYMENT_PROJECT_ID_OBJECT.getMessage());
        }

        @Test
        @DisplayName("null 금액으로 생성 시 예외가 발생한다.")
        void create_givenNullAmount_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> Payment.create(USER_ID, FUNDING_ID, PROJECT_ID, null, PG_INFO))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_PAYMENT_MONEY_OBJECT.getMessage());
        }

        @Test
        @DisplayName("null PG 정보로 생성 시 예외가 발생한다.")
        void create_givenNullPgInfo_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> Payment.create(USER_ID, FUNDING_ID, PROJECT_ID, AMOUNT, null))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_PG_INFO_OBJECT.getMessage());
        }
    }

    @Nested
    @DisplayName("결제 승인")
    class Complete {

        @Test
        @DisplayName("상태가 PENDING인 결제를 승인하면 상태가 COMPLETED로 변경되고 승인 시간이 설정된다.")
        void complete_givenPendingPayment_thenStatusChangedToCompletedAndApprovedAtSet() {
            /* given */
            Payment payment = createPayment();

            /* when */
            payment.complete(APPROVED_AT);

            /* then */
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
            assertThat(payment.getApprovedAt()).isEqualTo(APPROVED_AT);
        }

        @ParameterizedTest(name = "상태가 {0}인 결제를 승인 시도")
        @DisplayName("PENDING 상태가 아닌 결제를 승인하면 예외가 발생한다.")
        @EnumSource(value = PaymentStatus.class, names = {"COMPLETED", "REFUNDED"})
        void complete_givenNonPendingPayment_thenThrowException(PaymentStatus status) {
            /* given */
            Payment payment = createPaymentWithStatus(status);

            /* when */
            /* then */
            assertThatThrownBy(() -> payment.complete(APPROVED_AT))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.INVALID_PAYMENT_STATUS_TRANSITION.getMessage());
        }
    }

    @Nested
    @DisplayName("결제 실패")
    class Fail {

        @Test
        @DisplayName("상태가 PENDING인 결제를 실패 처리하면 상태가 FAILED로 변경된다.")
        void fail_givenPendingPayment_thenStatusChangedToFailed() {
            /* given */
            Payment payment = createPayment();

            /* when */
            payment.fail(FAILED_AT);

            /* then */
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        }

        @ParameterizedTest(name = "상태가 {0}인 결제를 실패 처리 시도")
        @DisplayName("PENDING 상태가 아닌 결제를 실패 처리하면 예외가 발생한다.")
        @EnumSource(value = PaymentStatus.class, names = {"COMPLETED", "REFUNDED"})
        void fail_givenNonPendingPayment_thenThrowException(PaymentStatus status) {
            /* given */
            Payment payment = createPaymentWithStatus(status);

            /* when */
            /* then */
            assertThatThrownBy(() -> payment.fail(FAILED_AT))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.INVALID_PAYMENT_STATUS_TRANSITION.getMessage());
        }
    }

    @Nested
    @DisplayName("결제 환불")
    class Refund {

        @Test
        @DisplayName("상태가 COMPLETED인 결제를 환불하면 상태가 REFUNDED로 변경되고 환불 계좌 정보가 설정된다.")
        void refund_givenCompletedPayment_thenStatusChangedToRefundedAndRefundAccountInfoSet() {
            /* given */
            Payment payment = createPaymentWithStatus(PaymentStatus.COMPLETED);

            /* when */
            payment.refund(REFUND_ACCOUNT_INFO, REFUND_REASON, REFUNDED_AT);

            /* then */
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
            assertThat(payment.getRefundAccountInfo()).isEqualTo(REFUND_ACCOUNT_INFO);
        }

        @ParameterizedTest(name = "상태가 {0}인 결제를 환불 시도")
        @DisplayName("COMPLETED 상태가 아닌 결제를 환불하면 예외가 발생한다.")
        @EnumSource(value = PaymentStatus.class, names = {"PENDING", "REFUNDED"})
        void refund_givenNonCompletedPayment_thenThrowException(PaymentStatus status) {
            /* given */
            Payment payment = createPaymentWithStatus(status);

            /* when */
            /* then */
            assertThatThrownBy(() -> payment.refund(REFUND_ACCOUNT_INFO, REFUND_REASON, REFUNDED_AT))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.INVALID_PAYMENT_STATUS_TRANSITION.getMessage());
        }
    }
}
