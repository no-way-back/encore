package com.nowayback.payment.domain.payment.entity;

import com.nowayback.payment.domain.exception.PaymentDomainErrorCode;
import com.nowayback.payment.domain.exception.PaymentDomainException;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
            assertThatThrownBy(() -> Payment.create(null, FUNDING_ID, AMOUNT, PG_INFO))
                    .isInstanceOf(PaymentDomainException.class)
                    .hasMessage(PaymentDomainErrorCode.NULL_USER_ID_OBJECT.getMessage());
        }

        @Test
        @DisplayName("null 펀딩 아이디로 생성 시 예외가 발생한다.")
        void create_givenNullFundingId_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> Payment.create(USER_ID, null, AMOUNT, PG_INFO))
                    .isInstanceOf(PaymentDomainException.class)
                    .hasMessage(PaymentDomainErrorCode.NULL_FUNDING_ID_OBJECT.getMessage());
        }

        @Test
        @DisplayName("null 금액으로 생성 시 예외가 발생한다.")
        void create_givenNullAmount_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> Payment.create(USER_ID, FUNDING_ID, null, PG_INFO))
                    .isInstanceOf(PaymentDomainException.class)
                    .hasMessage(PaymentDomainErrorCode.NULL_PAYMENT_MONEY_OBJECT.getMessage());
        }

        @Test
        @DisplayName("null PG 정보로 생성 시 예외가 발생한다.")
        void create_givenNullPgInfo_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> Payment.create(USER_ID, FUNDING_ID, AMOUNT, null))
                    .isInstanceOf(PaymentDomainException.class)
                    .hasMessage(PaymentDomainErrorCode.NULL_PG_INFO_OBJECT.getMessage());
        }
    }

    @Nested
    @DisplayName("결제 상태 변경")
    class ChangeStatus {

        @Test
        @DisplayName("null 결제 상태로 변경 시 예외가 발생한다.")
        void changeStatus_givenNullStatus_thenThrow() {
            /* given */
            Payment payment = createPayment();

            /* when / then */
            assertThatThrownBy(() -> payment.changeStatus(null))
                    .isInstanceOf(PaymentDomainException.class)
                    .hasMessage(PaymentDomainErrorCode.NULL_PAYMENT_STATUS_OBJECT.getMessage());
        }

        @ParameterizedTest(name = "{0} 상태에서 {1} 상태로 변경 시도")
        @DisplayName("유효한 상태 전이 시 결제 상태 변경에 성공한다.")
        @CsvSource({
                "PENDING, COMPLETED",
                "PENDING, FAILED",
                "COMPLETED, REFUNDED",
        })
        void changeStatus_givenValidTransition_thenSuccess(PaymentStatus initialStatus, PaymentStatus newStatus) {
            /* given */
            Payment payment = createPaymentWithStatus(initialStatus);

            /* when */
            payment.changeStatus(newStatus);

            /* then */
            assertThat(payment.getStatus()).isEqualTo(newStatus);
        }

        @ParameterizedTest(name = "{0} 상태에서 {1} 상태로 변경 시도")
        @DisplayName("유효하지 않은 상태 전이 시 예외가 발생한다.")
        @CsvSource({
                "PENDING, PENDING",
                "PENDING, REFUNDED",
                "COMPLETED, PENDING",
                "FAILED, COMPLETED",
                "REFUNDED, PENDING",
        })
        void changeStatus_givenInvalidTransition_thenThrowException(PaymentStatus initialStatus, PaymentStatus newStatus) {
            /* given */
            Payment payment = createPaymentWithStatus(initialStatus);

            /* when */
            /* then */
            assertThatThrownBy(() -> payment.changeStatus(newStatus))
                    .isInstanceOf(PaymentDomainException.class)
                    .hasMessage(PaymentDomainErrorCode.INVALID_PAYMENT_STATUS_TRANSITION.getMessage());
        }
    }

    @Nested
    @DisplayName("환불 계좌 정보 설정")
    class RefundAccount {

        @Test
        @DisplayName("유효한 환불 계좌 정보를 설정하면 성공한다.")
        void setRefundAccountInfo_givenValidInfo_thenSuccess() {
            /* given */
            Payment payment = createPayment();

            /* when */
            payment.setRefundAccountInfo(REFUND_ACCOUNT_INFO);

            /* then */
            assertThat(payment.getRefundAccountInfo()).isEqualTo(REFUND_ACCOUNT_INFO);
        }

        @Test
        @DisplayName("null 환불 계좌 정보 설정 시 예외가 발생한다.")
        void setRefundAccountInfo_givenNull_thenThrow() {
            /* given */
            Payment payment = createPayment();

            /* when / then */
            assertThatThrownBy(() -> payment.setRefundAccountInfo(null))
                    .isInstanceOf(PaymentDomainException.class)
                    .hasMessage(PaymentDomainErrorCode.NULL_REFUND_ACCOUNT_INFO_OBJECT.getMessage());
        }
    }
}
