package com.nowayback.reward.small.domain.stockreservation.entity;

import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import com.nowayback.reward.domain.stockreservation.vo.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StockReservationTest {

    @Nested
    @DisplayName("재고 예약 생성 테스트")
    class CreateStockReservation {

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            @DisplayName("옵션이 있는 재고 예약 생성 성공")
            void createWithOption() {
                // given
                UUID userId = UUID.randomUUID();
                UUID fundingId = UUID.randomUUID();
                UUID rewardId = UUID.randomUUID();
                UUID optionId = UUID.randomUUID();
                Integer quantity = 2;

                // when
                StockReservation reservation = StockReservation.create(
                        userId, fundingId, rewardId, optionId, quantity, LocalDateTime.now().plusMinutes(30)
                );

                // then
                assertThat(reservation).isNotNull();
                assertThat(reservation.getUserId().getId()).isEqualTo(userId);
                assertThat(reservation.getFundingId().getId()).isEqualTo(fundingId);
                assertThat(reservation.getRewardId().getId()).isEqualTo(rewardId);
                assertThat(reservation.getOptionId().getId()).isEqualTo(optionId);
                assertThat(reservation.getQuantity()).isEqualTo(quantity);
                assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
            }

            @Test
            @DisplayName("옵션이 없는 재고 예약 생성 성공")
            void createWithoutOption() {
                // given
                UUID userId = UUID.randomUUID();
                UUID fundingId = UUID.randomUUID();
                UUID rewardId = UUID.randomUUID();
                Integer quantity = 1;

                // when
                StockReservation reservation = StockReservation.create(
                        userId, fundingId, rewardId, null, quantity, LocalDateTime.now().plusMinutes(30)
                );

                // then
                assertThat(reservation).isNotNull();
                assertThat(reservation.getUserId().getId()).isEqualTo(userId);
                assertThat(reservation.getFundingId().getId()).isEqualTo(fundingId);
                assertThat(reservation.getRewardId().getId()).isEqualTo(rewardId);
                assertThat(reservation.getOptionId()).isNull();
                assertThat(reservation.getQuantity()).isEqualTo(quantity);
                assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
            }
        }
    }

    @Nested
    @DisplayName("상태 전이 테스트")
    class StatusTransition {

        private StockReservation pending() {
            return StockReservation.create(
                    UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null, 1,
                    LocalDateTime.now().plusMinutes(30)
            );
        }

        @Test
        @DisplayName("PENDING 예약을 확정하면 CONFIRMED가 되고 만료 시각이 지워진다")
        void confirm() {
            StockReservation reservation = pending();

            reservation.confirm();

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
            assertThat(reservation.getExpiresAt()).isNull();
        }

        @Test
        @DisplayName("이미 확정된 예약을 다시 확정해도 그대로 CONFIRMED다")
        void confirmTwice() {
            StockReservation reservation = pending();
            reservation.confirm();

            reservation.confirm();

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        }

        @Test
        @DisplayName("복원된 예약은 확정할 수 없다")
        void confirmRestored() {
            StockReservation reservation = pending();
            reservation.restore();

            org.assertj.core.api.Assertions.assertThatThrownBy(reservation::confirm)
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("확정된 예약도 환불 시 복원할 수 있다")
        void restoreConfirmed() {
            StockReservation reservation = pending();
            reservation.confirm();

            reservation.restore();

            assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RESTORED);
        }
    }
}
