package com.nowayback.payment.domain.settlement.entity;

import com.nowayback.payment.domain.exception.PaymentErrorCode;
import com.nowayback.payment.domain.exception.PaymentException;
import com.nowayback.payment.domain.settlement.vo.SettlementStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.*;
import static com.nowayback.payment.fixture.SettlementFixture.*;

@DisplayName("정산 엔티티")
class SettlementTest {

    @Nested
    @DisplayName("정산 엔티티 생성")
    class Create {

        @Test
        @DisplayName("모든 필드가 유효하면 정산 엔티티 생성에 성공한다.")
        void create_givenValidFields_thenSuccess() {
            /* given */
            /* when */
            Settlement settlement = createSettlement();

            /* then */
            assertThat(settlement.getProjectId()).isEqualTo(PROJECT_ID);
            assertThat(settlement.getTotalAmount()).isEqualTo(TOTAL_AMOUNT);
            assertThat(settlement.getServiceFee()).isEqualTo(SERVICE_FEE);
            assertThat(settlement.getPgFee()).isEqualTo(PG_FEE);
            assertThat(settlement.getNetAmount()).isEqualTo(NET_AMOUNT);
            assertThat(settlement.getAccountInfo()).isEqualTo(ACCOUNT_INFO);
            assertThat(settlement.getStatus()).isEqualTo(SettlementStatus.PROCESSING);
            assertThat(settlement.getRequestedAt()).isNotNull();
        }

        @Test
        @DisplayName("null 프로젝트 아이디로 생성 시 예외가 발생한다.")
        void create_givenNullProjectId_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> Settlement.create(null, TOTAL_AMOUNT, ACCOUNT_INFO, FEE_POLICY))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_PROJECT_ID_OBJECT.getMessage());
        }

        @Test
        @DisplayName("null 총액으로 생성 시 예외가 발생한다.")
        void create_givenNullTotalAmount_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> Settlement.create(PROJECT_ID, null, ACCOUNT_INFO, FEE_POLICY))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_SETTLEMENT_TOTAL_AMOUNT_OBJECT.getMessage());
        }

        @Test
        @DisplayName("null 계좌 정보로 생성 시 예외가 발생한다.")
        void create_givenNullAccountInfo_thenThrow() {
            /* given */
            /* when */
            /* then */
            assertThatThrownBy(() -> Settlement.create(PROJECT_ID, TOTAL_AMOUNT, null, FEE_POLICY))
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.NULL_SETTLEMENT_ACCOUNT_INFO_OBJECT.getMessage());
        }
    }

    @Nested
    @DisplayName("정산 완료")
    class Complete {

        @Test
        @DisplayName("정산 완료 처리 시 상태가 COMPLETED로 변경되고 완료 시간이 설정된다.")
        void complete_thenStatusCompletedAndCompletedAtSet() {
            /* given */
            Settlement settlement = createSettlement();

            /* when */
            settlement.complete();

            /* then */
            assertThat(settlement.getStatus()).isEqualTo(SettlementStatus.COMPLETED);
            assertThat(settlement.getCompletedAt()).isNotNull();
        }

        @ParameterizedTest(name = "상태가 {0}일 때")
        @DisplayName("유효하지 않은 상태에서 정산 완료 처리 시 예외가 발생한다.")
        @EnumSource(value = SettlementStatus.class, names = {"COMPLETED", "FAILED"})
        void complete_givenInvalidStatus_thenThrow(SettlementStatus status) {
            /* given */
            Settlement settlement = createSettlementWithStatus(status);

            /* when */
            /* then */
            assertThatThrownBy(settlement::complete)
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.INVALID_SETTLEMENT_STATUS_TRANSITION.getMessage());
        }
    }

    @Nested
    @DisplayName("정산 실패")
    class Fail {

        @Test
        @DisplayName("정산 실패 처리 시 상태가 FAILED로 변경되고 실패 시간이 설정된다.")
        void fail_thenStatusFailedAndFailedAtSet() {
            /* given */
            Settlement settlement = createSettlement();

            /* when */
            settlement.fail();

            /* then */
            assertThat(settlement.getStatus()).isEqualTo(SettlementStatus.FAILED);
            assertThat(settlement.getFailedAt()).isNotNull();
        }

        @ParameterizedTest(name = "상태가 {0}일 때")
        @DisplayName("유효하지 않은 상태에서 정산 실패 처리 시 예외가 발생한다.")
        @EnumSource(value = SettlementStatus.class, names = {"COMPLETED", "FAILED"})
        void fail_givenInvalidStatus_thenThrow(SettlementStatus status) {
            /* given */
            Settlement settlement = createSettlementWithStatus(status);

            /* when */
            /* then */
            assertThatThrownBy(settlement::fail)
                    .isInstanceOf(PaymentException.class)
                    .hasMessage(PaymentErrorCode.INVALID_SETTLEMENT_STATUS_TRANSITION.getMessage());
        }
    }
}
