package com.nowayback.payment.domain.settlement.entity;

import com.nowayback.payment.domain.exception.PaymentErrorCode;
import com.nowayback.payment.domain.exception.PaymentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static com.nowayback.payment.fixture.SettlementStatusLogFixture.*;

@DisplayName("정산 상태 기록 엔티티")
class SettlementStatusLogTest {

    @Nested
    @DisplayName("정산 상태 로그 엔티티 생성")
    class Create {

        @Test
        @DisplayName("모든 필드가 유효하면 정산 상태 로그 엔티티 생성에 성공한다.")
        void create_givenValidFields_thenSuccess() {
            /* given */
            /* when */
            SettlementStatusLog settlementStatusLog = createSettlementStatusLog();

            /* then */
            assertThat(settlementStatusLog.getSettlementId()).isEqualTo(SETTLEMENT_ID);
            assertThat(settlementStatusLog.getPrevStatus()).isEqualTo(PREV_STATUS);
            assertThat(settlementStatusLog.getCurrStatus()).isEqualTo(CURR_STATUS);
            assertThat(settlementStatusLog.getReason()).isEqualTo(REASON);
            assertThat(settlementStatusLog.getAmount()).isEqualTo(AMOUNT);
        }

        @Test
        @DisplayName("null 정산 아이디로 생성 시 예외가 발생한다.")
        void create_givenNullSettlementId_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> SettlementStatusLog.create(null, PREV_STATUS, CURR_STATUS, REASON, AMOUNT))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_SETTLEMENT_ID_OBJECT.getMessage());
        }

        @Test
        @DisplayName("null 이전 정산 상태로 생성 시 예외가 발생한다.")
        void create_givenNullPrevStatus_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> SettlementStatusLog.create(SETTLEMENT_ID, null, CURR_STATUS, REASON, AMOUNT))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_PREV_SETTLEMENT_STATUS_OBJECT.getMessage());
        }

        @Test
        @DisplayName("null 현재 정산 상태로 생성 시 예외가 발생한다.")
        void create_givenNullCurrStatus_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> SettlementStatusLog.create(SETTLEMENT_ID, PREV_STATUS, null, REASON, AMOUNT))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_CURR_SETTLEMENT_STATUS_OBJECT.getMessage());
        }

        @Test
        @DisplayName("null 사유로 생성 시 예외가 발생한다.")
        void create_givenNullOrBlankReason_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> SettlementStatusLog.create(SETTLEMENT_ID, PREV_STATUS, CURR_STATUS, null, AMOUNT))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_SETTLEMENT_STATUS_LOG_REASON.getMessage());
        }

        @Test
        @DisplayName("공백 사유로 생성 시 예외가 발생한다.")
        void create_givenBlankReason_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> SettlementStatusLog.create(SETTLEMENT_ID, PREV_STATUS, CURR_STATUS, "   ", AMOUNT))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_SETTLEMENT_STATUS_LOG_REASON.getMessage());
        }

        @Test
        @DisplayName("null 금액으로 생성 시 예외가 발생한다.")
        void create_givenNullAmount_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> SettlementStatusLog.create(SETTLEMENT_ID, PREV_STATUS, CURR_STATUS, REASON, null))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_SETTLEMENT_AMOUNT_OBJECT.getMessage());
        }
    }
}