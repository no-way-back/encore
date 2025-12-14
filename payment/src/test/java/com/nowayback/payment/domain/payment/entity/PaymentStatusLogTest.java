package com.nowayback.payment.domain.payment.entity;

import com.nowayback.payment.domain.exception.PaymentErrorCode;
import com.nowayback.payment.domain.exception.PaymentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static com.nowayback.payment.fixture.PaymentStatusLogFixture.*;

@DisplayName("결제 상태 기록 엔티티")
class PaymentStatusLogTest {

    @Nested
    @DisplayName("결제 상태 로그 엔티티 생성")
    class Create {

        @Test
        @DisplayName("모든 필드가 유효하면 결제 상태 로그 엔티티 생성에 성공한다.")
        void create_givenValidFields_thenSuccess() {
            /* given */
            /* when */
            PaymentStatusLog paymentStatusLog = createPaymentStatusLog();

            /* then */
            assertThat(paymentStatusLog.getPaymentId()).isEqualTo(PAYMENT_ID);
            assertThat(paymentStatusLog.getPrevStatus()).isEqualTo(PREV_STATUS);
            assertThat(paymentStatusLog.getCurrStatus()).isEqualTo(CURR_STATUS);
            assertThat(paymentStatusLog.getReason()).isEqualTo(REASON);
            assertThat(paymentStatusLog.getAmount()).isEqualTo(AMOUNT);
        }

        @Test
        @DisplayName("null 결제 아이디로 생성 시 예외가 발생한다.")
        void create_givenNullPaymentId_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> PaymentStatusLog.create(null, PREV_STATUS, CURR_STATUS, REASON, AMOUNT))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_PAYMENT_ID_OBJECT.getMessage());
        }

        @Test
        @DisplayName("null 이전 결제 상태로 생성 시 예외가 발생한다.")
        void create_givenNullPrevStatus_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> PaymentStatusLog.create(PAYMENT_ID, null, CURR_STATUS, REASON, AMOUNT))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_PREV_PAYMENT_STATUS_OBJECT.getMessage());
        }

        @Test
        @DisplayName("null 현재 결제 상태로 생성 시 예외가 발생한다.")
        void create_givenNullCurrStatus_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> PaymentStatusLog.create(PAYMENT_ID, PREV_STATUS, null, REASON, AMOUNT))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_CURR_PAYMENT_STATUS_OBJECT.getMessage());
        }

        @Test
        @DisplayName("null 금액으로 생성 시 예외가 발생한다.")
        void create_givenNullAmount_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> PaymentStatusLog.create(PAYMENT_ID, PREV_STATUS, CURR_STATUS, REASON, null))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_PAYMENT_AMOUNT_OBJECT.getMessage());
        }
    }
}