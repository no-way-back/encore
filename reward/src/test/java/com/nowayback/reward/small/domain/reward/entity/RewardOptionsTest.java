package com.nowayback.reward.small.domain.reward.entity;

import com.nowayback.reward.application.reward.command.UpdateRewardOptionCommand;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.reward.entity.RewardOptions;
import com.nowayback.reward.domain.reward.entity.Rewards;
import com.nowayback.reward.domain.reward.vo.SaleStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.nowayback.reward.fixture.StockReserveFixture.createRewardWithOptions;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RewardOptionsTest {

    private Rewards reward;
    private RewardOptions option;

    @BeforeEach
    void setUp() {
        reward = createRewardWithOptions();
        option = reward.getOptionList().get(0);
    }

    @Nested
    @DisplayName("재고 차감 테스트")
    class DecreaseStock {

        @Test
        @DisplayName("재고 차감 성공")
        void decreaseStock_success() {
            // given
            int initialStock = option.getStock().getQuantity();
            int decreaseQuantity = 5;

            // when
            option.decreaseStock(decreaseQuantity);

            // then
            assertThat(option.getStock().getQuantity()).isEqualTo(initialStock - decreaseQuantity);
            assertThat(option.getStatus()).isEqualTo(SaleStatus.AVAILABLE);
        }

        @Test
        @DisplayName("재고를 전부 차감하면 SOLD_OUT 상태로 변경")
        void decreaseStock_soldOut() {
            // given
            int currentStock = option.getStock().getQuantity();

            // when
            option.decreaseStock(currentStock);

            // then
            assertThat(option.getStock().getQuantity()).isEqualTo(0);
            assertThat(option.getStatus()).isEqualTo(SaleStatus.SOLD_OUT);
        }

        @Test
        @DisplayName("재고보다 많은 수량 차감 시 예외 발생")
        void decreaseStock_insufficientStock() {
            // given
            int currentStock = option.getStock().getQuantity();

            // when & then
            assertThatThrownBy(() -> option.decreaseStock(currentStock + 1))
                    .isInstanceOf(RewardException.class);
        }
    }

    @Nested
    @DisplayName("재고 복원 테스트")
    class RestoreStock {

        @Test
        @DisplayName("재고 복원 성공")
        void restoreStock_success() {
            // given
            int initialStock = option.getStock().getQuantity();
            option.decreaseStock(10);

            // when
            option.restoreStock(10);

            // then
            assertThat(option.getStock().getQuantity()).isEqualTo(initialStock);
            assertThat(option.getStatus()).isEqualTo(SaleStatus.AVAILABLE);
        }

        @Test
        @DisplayName("SOLD_OUT 상태에서 재고 복원 시 AVAILABLE로 변경")
        void restoreStock_fromSoldOut() {
            // given
            int currentStock = option.getStock().getQuantity();
            option.decreaseStock(currentStock);
            assertThat(option.getStatus()).isEqualTo(SaleStatus.SOLD_OUT);

            // when
            option.restoreStock(5);

            // then
            assertThat(option.getStock().getQuantity()).isEqualTo(5);
            assertThat(option.getStatus()).isEqualTo(SaleStatus.AVAILABLE);
        }
    }

    @Nested
    @DisplayName("옵션 수정 테스트")
    class Update {

        @Test
        @DisplayName("옵션 이름 수정 성공")
        void update_name() {
            // given
            UpdateRewardOptionCommand command = new UpdateRewardOptionCommand(
                    option.getId(), "새로운 이름", null, null, null, null
            );

            // when
            option.update(command);

            // then
            assertThat(option.getName()).isEqualTo("새로운 이름");
        }

        @Test
        @DisplayName("옵션 추가금 수정 성공")
        void update_additionalPrice() {
            // given
            UpdateRewardOptionCommand command = new UpdateRewardOptionCommand(
                    option.getId(), null, 5000L, null, null, null
            );

            // when
            option.update(command);

            // then
            assertThat(option.getAdditionalPrice().getAmount()).isEqualTo(5000L);
        }

        @Test
        @DisplayName("옵션 재고 수정 성공")
        void update_stock() {
            // given
            UpdateRewardOptionCommand command = new UpdateRewardOptionCommand(
                    option.getId(), null, null, 999, null, null
            );

            // when
            option.update(command);

            // then
            assertThat(option.getStock().getQuantity()).isEqualTo(999);
        }
    }

    @Nested
    @DisplayName("총 가격 계산 테스트")
    class CalculateTotalAmount {

        @Test
        @DisplayName("추가금 없는 옵션 총 가격 계산")
        void calculateTotalAmount_noAdditionalPrice() {
            // given
            RewardOptions noExtraOption = reward.getOptionList().get(0);
            int quantity = 2;
            Long rewardPrice = reward.getPrice().getAmount();

            // when
            Long totalAmount = noExtraOption.calculateTotalAmount(quantity);

            // then
            assertThat(totalAmount).isEqualTo(rewardPrice * quantity);
        }

        @Test
        @DisplayName("추가금 있는 옵션 총 가격 계산")
        void calculateTotalAmount_withAdditionalPrice() {
            // given
            RewardOptions extraOption = reward.getOptionList().get(2);
            int quantity = 3;
            Long rewardPrice = reward.getPrice().getAmount();
            Long additionalPrice = extraOption.getAdditionalPrice().getAmount();

            // when
            Long totalAmount = extraOption.calculateTotalAmount(quantity);

            // then
            assertThat(totalAmount).isEqualTo((rewardPrice + additionalPrice) * quantity);
        }
    }
}