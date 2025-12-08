package com.nowayback.reward.small.domain.reward.entity;

import com.nowayback.reward.application.reward.command.UpdateRewardCommand;
import com.nowayback.reward.application.reward.command.UpdateRewardOptionCommand;
import com.nowayback.reward.domain.exception.RewardErrorCode;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.reward.command.CreateRewardCommand;
import com.nowayback.reward.domain.reward.command.CreateRewardOptionCommand;
import com.nowayback.reward.domain.reward.entity.RewardOptions;
import com.nowayback.reward.domain.reward.entity.Rewards;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import static com.nowayback.reward.fixture.RewardFixture.*;
import static org.assertj.core.api.Assertions.*;

class RewardsTest {

    @Nested
    @DisplayName("리워드 생성 테스트")
    class CreateReward {

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            @DisplayName("리워드 생성 성공")
            void createReward() {
                // given
                CreateRewardCommand command = createCommand();

                // when
                Rewards reward = Rewards.create(command);

                // then
                assertThat(reward).isNotNull();
                assertThat(reward.getName()).isEqualTo("테스트 리워드");
                assertThat(reward.getPrice().getAmount()).isEqualTo(25000);
                assertThat(reward.getStock().getQuantity()).isEqualTo(100);
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Failure {

            @Test
            @DisplayName("금액이 최소 금액 미만일 때 예외 발생")
            void priceBelowMinimum() {
                // given
                CreateRewardCommand command = createCommand("리워드", 500L, 100);

                // when & then
                assertThatThrownBy(() -> Rewards.create(command))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.PRICE_BELOW_MINIMUM);
            }

            @Test
            @DisplayName("재고가 최소 수량 미만일 때 예외 발생")
            void stockBelowMinimum() {
                // given
                CreateRewardCommand command = createCommand("리워드", 25000L, 0);

                // when & then
                assertThatThrownBy(() -> Rewards.create(command))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.STOCK_BELOW_MINIMUM);
            }

            @Test
            @DisplayName("금액이 음수일 때 예외 발생")
            void negativePrice() {
                // given
                CreateRewardCommand command = createCommand("리워드", -1000L, 100);

                // when & then
                assertThatThrownBy(() -> Rewards.create(command))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.NEGATIVE_MONEY_AMOUNT);
            }

            @Test
            @DisplayName("재고가 음수일 때 예외 발생")
            void negativeStock() {
                // given
                CreateRewardCommand command = createCommand("리워드", 25000L, -10);

                // when & then
                assertThatThrownBy(() -> Rewards.create(command))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.NEGATIVE_STOCK_QUANTITY);
            }

            @Test
            @DisplayName("배송비가 음수일 때 예외 발생")
            void negativeShippingFee() {
                // given
                CreateRewardCommand command = createCommandWithShippingPolicy(-1000, 30000);

                // when & then
                assertThatThrownBy(() -> Rewards.create(command))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.NEGATIVE_SHIPPING_FEE);
            }

            @Test
            @DisplayName("옵션 추가 가격이 음수일 때 예외 발생")
            void negativeOptionAdditionalPrice() {
                // given
                Rewards reward = Rewards.create(createCommand());
                CreateRewardOptionCommand optionCommand = createOptionCommand("S", -500L, 20, 1);

                // when & then
                assertThatThrownBy(() -> reward.addOption(optionCommand))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.NEGATIVE_MONEY_AMOUNT);
            }
        }
    }

    @Nested
    @DisplayName("옵션 추가 테스트")
    class AddOption {

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            @DisplayName("옵션 추가 성공")
            void addOption() {
                // given
                Rewards reward = Rewards.create(createCommand());
                CreateRewardOptionCommand optionCommand = createOptionCommand("S", 0L, 20, 1);

                // when
                RewardOptions option = reward.addOption(optionCommand);

                // then
                assertThat(option).isNotNull();
                assertThat(option.getName()).isEqualTo("S");
                assertThat(reward.getOptionList()).hasSize(1);
            }

            @Test
            @DisplayName("여러 옵션 한 번에 추가 성공")
            void addMultipleOptions() {
                // given
                Rewards reward = Rewards.create(createCommand());
                List<CreateRewardOptionCommand> options = List.of(
                        createOptionCommand("S", 0L, 20, 1),
                        createOptionCommand("M", 0L, 30, 2),
                        createOptionCommand("L", 2000L, 25, 3)
                );

                // when
                reward.addOptionList(options);

                // then
                assertThat(reward.getOptionList()).hasSize(3);
            }

            @Test
            @DisplayName("null 옵션 리스트는 무시됨")
            void nullOptionList() {
                // given
                Rewards reward = Rewards.create(createCommand());

                // when & then
                assertThatCode(() -> reward.addOptionList(null))
                        .doesNotThrowAnyException();
                assertThat(reward.getOptionList()).isEmpty();
            }

            @Test
            @DisplayName("빈 옵션 리스트는 무시됨")
            void emptyOptionList() {
                // given
                Rewards reward = Rewards.create(createCommand());

                // when & then
                assertThatCode(() -> reward.addOptionList(List.of()))
                        .doesNotThrowAnyException();
                assertThat(reward.getOptionList()).isEmpty();
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Failure {

            @Test
            @DisplayName("옵션명 중복 시 예외 발생")
            void duplicateOptionName() {
                // given
                Rewards reward = Rewards.create(createCommand());
                CreateRewardOptionCommand option1 = createOptionCommand("S", 0L, 20, 1);
                CreateRewardOptionCommand option2 = createOptionCommand("S", 0L, 30, 2);

                reward.addOption(option1);

                // when & then
                assertThatThrownBy(() -> reward.addOption(option2))
                        .isInstanceOf(RewardException.class)
                        .hasMessageContaining("이미 존재하는 옵션명입니다");
            }

            @Test
            @DisplayName("표시 순서 중복 시 예외 발생")
            void duplicateDisplayOrder() {
                // given
                Rewards reward = Rewards.create(createCommand());
                CreateRewardOptionCommand option1 = createOptionCommand("S", 0L, 20, 1);
                CreateRewardOptionCommand option2 = createOptionCommand("M", 0L, 30, 1);

                reward.addOption(option1);

                // when & then
                assertThatThrownBy(() -> reward.addOption(option2))
                        .isInstanceOf(RewardException.class)
                        .hasMessageContaining("이미 사용 중인 표시 순서입니다");
            }

            @Test
            @DisplayName("옵션 개수 초과 시 예외 발생")
            void optionCountExceeded() {
                // given
                Rewards reward = Rewards.create(createCommand());
                List<CreateRewardOptionCommand> options = createOptionCommands(21);

                // when & then
                assertThatThrownBy(() -> reward.addOptionList(options))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.OPTION_COUNT_EXCEEDED);
            }
        }
    }

    @Nested
    @DisplayName("리워드 수정 테스트")
    class UpdateReward {

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            @DisplayName("모든 필드 수정 성공")
            void updateAllFields() {
                // given
                Rewards reward = Rewards.create(createCommand());
                UpdateRewardCommand command = createUpdateCommand(reward.getId());

                // when
                reward.update(command);

                // then
                assertThat(reward.getName()).isEqualTo("수정된 리워드");
                assertThat(reward.getDescription()).isEqualTo("수정된 설명");
                assertThat(reward.getPrice().getAmount()).isEqualTo(30000);
                assertThat(reward.getStock().getQuantity()).isEqualTo(10);
            }

            @Test
            @DisplayName("일부 필드만 수정 - null 필드는 기존 값 유지")
            void updatePartialFields() {
                // given
                Rewards reward = Rewards.create(createCommand("원래 이름", 25000L, 100));
                UpdateRewardCommand command = createUpdateCommand(reward.getId(), "수정된 이름", null, null);

                // when
                reward.update(command);

                // then
                assertThat(reward.getName()).isEqualTo("수정된 이름");
                assertThat(reward.getPrice().getAmount()).isEqualTo(25000); // 유지
                assertThat(reward.getStock().getQuantity()).isEqualTo(100); // 유지
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Failure {

            @Test
            @DisplayName("가격이 최소값 미만으로 수정 시 예외 발생")
            void updatePriceBelowMinimum() {
                // given
                Rewards reward = Rewards.create(createCommand());
                UpdateRewardCommand command = createUpdateCommand(reward.getId(), null, 500L, null);

                // when & then
                assertThatThrownBy(() -> reward.update(command))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.PRICE_BELOW_MINIMUM);
            }

            @Test
            @DisplayName("재고가 최소값 미만으로 수정 시 예외 발생")
            void updateStockBelowMinimum() {
                // given
                Rewards reward = Rewards.create(createCommand());
                UpdateRewardCommand command = createUpdateCommand(reward.getId(), null, null, 0);

                // when & then
                assertThatThrownBy(() -> reward.update(command))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.STOCK_BELOW_MINIMUM);
            }
        }
    }

    @Nested
    @DisplayName("옵션 수정 테스트")
    class UpdateOption {

        private void setOptionId(RewardOptions option, UUID id) throws Exception {
            Field idField = RewardOptions.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(option, id);
        }

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            @DisplayName("옵션 모든 필드 수정 성공")
            void updateAllFields() throws Exception {
                // given
                Rewards reward = Rewards.create(createCommand());
                RewardOptions option = reward.addOption(createOptionCommand("S", 0L, 20, 1));
                UUID optionId = UUID.randomUUID();
                setOptionId(option, optionId);

                UpdateRewardOptionCommand optionCommand = createUpdateOptionCommand(optionId);
                UpdateRewardCommand command = new UpdateRewardCommand(
                        reward.getId(), null, null, null, null, null, null, null, null,
                        List.of(optionCommand)
                );

                // when
                reward.update(command);

                // then
                assertThat(option.getName()).isEqualTo("수정된 옵션");
                assertThat(option.getAdditionalPrice().getAmount()).isEqualTo(1000);
                assertThat(option.getStock().getQuantity()).isEqualTo(10);
            }

            @Test
            @DisplayName("옵션 일부 필드만 수정 - null 필드는 기존 값 유지")
            void updatePartialFields() throws Exception {
                // given
                Rewards reward = Rewards.create(createCommand());
                RewardOptions option = reward.addOption(createOptionCommand("S", 1000L, 20, 1));
                UUID optionId = UUID.randomUUID();
                setOptionId(option, optionId);

                UpdateRewardOptionCommand optionCommand = createUpdateOptionCommand(optionId, "Small", null, null);
                UpdateRewardCommand command = new UpdateRewardCommand(
                        reward.getId(), null, null, null, null, null, null, null, null,
                        List.of(optionCommand)
                );

                // when
                reward.update(command);

                // then
                assertThat(option.getName()).isEqualTo("Small");
                assertThat(option.getAdditionalPrice().getAmount()).isEqualTo(1000); // 유지
                assertThat(option.getStock().getQuantity()).isEqualTo(20); // 유지
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Failure {

            @Test
            @DisplayName("옵션명을 중복되게 수정 시 예외 발생")
            void updateOptionNameToDuplicate() throws Exception {
                // given
                Rewards reward = Rewards.create(createCommand());
                RewardOptions option1 = reward.addOption(createOptionCommand("S", 0L, 20, 1));
                reward.addOption(createOptionCommand("M", 0L, 30, 2));
                UUID optionId1 = UUID.randomUUID();
                setOptionId(option1, optionId1);

                UpdateRewardOptionCommand optionCommand = createUpdateOptionCommand(optionId1, "M", null, null);
                UpdateRewardCommand command = new UpdateRewardCommand(
                        reward.getId(), null, null, null, null, null, null, null, null,
                        List.of(optionCommand)
                );

                // when & then
                assertThatThrownBy(() -> reward.update(command))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.DUPLICATE_OPTION_NAME);
            }

            @Test
            @DisplayName("옵션 재고를 최소값 미만으로 수정 시 예외 발생")
            void updateOptionStockBelowMinimum() throws Exception {
                // given
                Rewards reward = Rewards.create(createCommand());
                RewardOptions option = reward.addOption(createOptionCommand("S", 0L, 20, 1));
                UUID optionId = UUID.randomUUID();
                setOptionId(option, optionId);

                UpdateRewardOptionCommand optionCommand = createUpdateOptionCommand(optionId, null, null, 0);
                UpdateRewardCommand command = new UpdateRewardCommand(
                        reward.getId(), null, null, null, null, null, null, null, null,
                        List.of(optionCommand)
                );

                // when & then
                assertThatThrownBy(() -> reward.update(command))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.STOCK_BELOW_MINIMUM);
            }
        }
    }
}