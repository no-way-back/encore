package com.nowayback.reward.small.domain.stockreservation.entity;

import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import com.nowayback.reward.domain.stockreservation.vo.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
                        userId, fundingId, rewardId, optionId, quantity
                );

                // then
                assertThat(reservation).isNotNull();
                assertThat(reservation.getUserId().getId()).isEqualTo(userId);
                assertThat(reservation.getFundingId().getId()).isEqualTo(fundingId);
                assertThat(reservation.getRewardId().getId()).isEqualTo(rewardId);
                assertThat(reservation.getOptionId().getId()).isEqualTo(optionId);
                assertThat(reservation.getQuantity()).isEqualTo(quantity);
                assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.DEDUCTED);
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
                        userId, fundingId, rewardId, null, quantity
                );

                // then
                assertThat(reservation).isNotNull();
                assertThat(reservation.getUserId().getId()).isEqualTo(userId);
                assertThat(reservation.getFundingId().getId()).isEqualTo(fundingId);
                assertThat(reservation.getRewardId().getId()).isEqualTo(rewardId);
                assertThat(reservation.getOptionId()).isNull();
                assertThat(reservation.getQuantity()).isEqualTo(quantity);
                assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.DEDUCTED);
            }
        }
    }
}