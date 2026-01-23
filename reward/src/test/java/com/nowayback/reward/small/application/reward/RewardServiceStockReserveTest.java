package com.nowayback.reward.small.application.reward;

import com.nowayback.reward.application.reward.RewardStockTransactionService;
import com.nowayback.reward.application.reward.command.StockReserveCommand;
import com.nowayback.reward.application.reward.dto.StockReserveResult;
import com.nowayback.reward.domain.exception.RewardErrorCode;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.reward.entity.RewardOptions;
import com.nowayback.reward.domain.reward.entity.Rewards;
import com.nowayback.reward.application.reward.repository.RewardRepository;
import com.nowayback.reward.domain.reward.vo.SaleStatus;
import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import com.nowayback.reward.application.stockreservation.repository.StockReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.nowayback.reward.fixture.StockReserveFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardServiceStockReserveTest {

    @Mock
    private RewardRepository rewardRepository;

    @Mock
    private StockReservationRepository stockReservationRepository;

    @InjectMocks
    private RewardStockTransactionService transactionService;

    @Nested
    @DisplayName("재고 예약 테스트")
    class ReserveStock {

        @Nested
        @DisplayName("성공 케이스")
        class Success {

            @Test
            @DisplayName("옵션 없는 리워드 재고 예약 성공")
            void reserveStockWithoutOption() {
                // given
                UUID userId = UUID.randomUUID();
                UUID fundingId = UUID.randomUUID();
                Rewards reward = createRewardWithoutOption(100);
                StockReserveCommand.StockReserveItemCommand item =
                        new StockReserveCommand.StockReserveItemCommand(reward.getId(), null, 2);

                when(rewardRepository.findByIdWithOptions(reward.getId())).thenReturn(Optional.of(reward));
                when(stockReservationRepository.save(any(StockReservation.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                // when
                StockReserveResult.ReservationWithPrice result =
                        transactionService.reserveStockForItem(userId, fundingId, item);

                // then
                assertThat(result.reservation().getFundingId().getId()).isEqualTo(fundingId);
                assertThat(result.reservation().getQuantity()).isEqualTo(2);
                assertThat(result.itemAmount()).isEqualTo(60000);

                assertThat(reward.getStock().getQuantity()).isEqualTo(98);
                assertThat(reward.getStatus()).isEqualTo(SaleStatus.AVAILABLE);

                verify(rewardRepository, times(1)).findByIdWithOptions(reward.getId());
                verify(stockReservationRepository, times(1)).save(any(StockReservation.class));
            }

            @Test
            @DisplayName("옵션 없는 리워드 마지막 재고 구매 - SOLD_OUT 처리")
            void reserveLastStockWithoutOption() {
                // given
                UUID userId = UUID.randomUUID();
                UUID fundingId = UUID.randomUUID();
                Rewards reward = createRewardWithoutOption(5);
                StockReserveCommand.StockReserveItemCommand item =
                        new StockReserveCommand.StockReserveItemCommand(reward.getId(), null, 5);

                when(rewardRepository.findByIdWithOptions(reward.getId())).thenReturn(Optional.of(reward));
                when(stockReservationRepository.save(any(StockReservation.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                // when
                StockReserveResult.ReservationWithPrice result =
                        transactionService.reserveStockForItem(userId, fundingId, item);

                // then
                assertThat(result.reservation().getFundingId().getId()).isEqualTo(fundingId);
                assertThat(reward.getStock().getQuantity()).isEqualTo(0);
                assertThat(reward.getStatus()).isEqualTo(SaleStatus.SOLD_OUT);

                verify(rewardRepository, times(1)).findByIdWithOptions(reward.getId());
                verify(stockReservationRepository, times(1)).save(any(StockReservation.class));
            }

            @Test
            @DisplayName("옵션 있는 리워드 재고 예약 성공 - 추가금 없음")
            void reserveStockWithOption() {
                // given
                UUID userId = UUID.randomUUID();
                UUID fundingId = UUID.randomUUID();
                Rewards reward = createRewardWithOptions();
                RewardOptions option = reward.getOptionList().get(0);
                StockReserveCommand.StockReserveItemCommand item =
                        new StockReserveCommand.StockReserveItemCommand(reward.getId(), option.getId(), 2);

                when(rewardRepository.findByIdWithOptions(reward.getId())).thenReturn(Optional.of(reward));
                when(stockReservationRepository.save(any(StockReservation.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                // when
                StockReserveResult.ReservationWithPrice result =
                        transactionService.reserveStockForItem(userId, fundingId, item);

                // then
                assertThat(result.reservation().getFundingId().getId()).isEqualTo(fundingId);
                assertThat(result.reservation().getQuantity()).isEqualTo(2);
                assertThat(result.itemAmount()).isEqualTo(100000);

                assertThat(option.getStock().getQuantity()).isEqualTo(28);
                assertThat(option.getStatus()).isEqualTo(SaleStatus.AVAILABLE);
                assertThat(reward.getStatus()).isEqualTo(SaleStatus.AVAILABLE);

                verify(rewardRepository, times(1)).findByIdWithOptions(reward.getId());
                verify(stockReservationRepository, times(1)).save(any(StockReservation.class));
            }

            @Test
            @DisplayName("옵션 마지막 재고 구매 - 옵션만 SOLD_OUT 처리")
            void reserveLastStockOption() {
                // given
                UUID userId = UUID.randomUUID();
                UUID fundingId = UUID.randomUUID();
                Rewards reward = createRewardWithOptions();
                RewardOptions option = reward.getOptionList().get(2);
                StockReserveCommand.StockReserveItemCommand item =
                        new StockReserveCommand.StockReserveItemCommand(reward.getId(), option.getId(), 20);

                when(rewardRepository.findByIdWithOptions(reward.getId())).thenReturn(Optional.of(reward));
                when(stockReservationRepository.save(any(StockReservation.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                // when
                StockReserveResult.ReservationWithPrice result =
                        transactionService.reserveStockForItem(userId, fundingId, item);

                // then
                assertThat(option.getStock().getQuantity()).isEqualTo(0);
                assertThat(option.getStatus()).isEqualTo(SaleStatus.SOLD_OUT);
                assertThat(reward.getStatus()).isEqualTo(SaleStatus.AVAILABLE);

                verify(rewardRepository, times(1)).findByIdWithOptions(reward.getId());
                verify(stockReservationRepository, times(1)).save(any(StockReservation.class));
            }

            @Test
            @DisplayName("모든 옵션 품절 시 리워드도 SOLD_OUT 처리")
            void allOptionsSoldOut() {
                // given
                UUID userId = UUID.randomUUID();
                UUID fundingId = UUID.randomUUID();
                Rewards reward = createRewardWithOptions();

                RewardOptions option1 = reward.getOptionList().get(0);
                RewardOptions option2 = reward.getOptionList().get(1);
                RewardOptions option3 = reward.getOptionList().get(2);

                when(rewardRepository.findByIdWithOptions(reward.getId())).thenReturn(Optional.of(reward));
                when(stockReservationRepository.save(any(StockReservation.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                // when - S 품절
                StockReserveCommand.StockReserveItemCommand item1 =
                        new StockReserveCommand.StockReserveItemCommand(reward.getId(), option1.getId(), 30);
                transactionService.reserveStockForItem(userId, fundingId, item1);

                assertThat(option1.getStatus()).isEqualTo(SaleStatus.SOLD_OUT);
                assertThat(reward.getStatus()).isEqualTo(SaleStatus.AVAILABLE);

                // when - M 품절
                StockReserveCommand.StockReserveItemCommand item2 =
                        new StockReserveCommand.StockReserveItemCommand(reward.getId(), option2.getId(), 50);
                transactionService.reserveStockForItem(userId, fundingId, item2);

                assertThat(option2.getStatus()).isEqualTo(SaleStatus.SOLD_OUT);
                assertThat(reward.getStatus()).isEqualTo(SaleStatus.AVAILABLE);

                // when - L 품절 (마지막 옵션)
                StockReserveCommand.StockReserveItemCommand item3 =
                        new StockReserveCommand.StockReserveItemCommand(reward.getId(), option3.getId(), 20);
                transactionService.reserveStockForItem(userId, fundingId, item3);

                // then - 모든 옵션 품절 → 리워드도 품절
                assertThat(option3.getStatus()).isEqualTo(SaleStatus.SOLD_OUT);
                assertThat(reward.getStatus()).isEqualTo(SaleStatus.SOLD_OUT);
            }

            @Test
            @DisplayName("옵션 있는 리워드 재고 예약 성공 - 추가금 있음")
            void reserveStockWithOptionAdditionalPrice() {
                // given
                UUID userId = UUID.randomUUID();
                UUID fundingId = UUID.randomUUID();
                Rewards reward = createRewardWithOptions();
                RewardOptions option = reward.getOptionList().get(2);
                StockReserveCommand.StockReserveItemCommand item =
                        new StockReserveCommand.StockReserveItemCommand(reward.getId(), option.getId(), 3);

                when(rewardRepository.findByIdWithOptions(reward.getId())).thenReturn(Optional.of(reward));
                when(stockReservationRepository.save(any(StockReservation.class)))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                // when
                StockReserveResult.ReservationWithPrice result =
                        transactionService.reserveStockForItem(userId, fundingId, item);

                // then
                assertThat(result.reservation().getFundingId().getId()).isEqualTo(fundingId);
                assertThat(result.reservation().getQuantity()).isEqualTo(3);
                assertThat(result.itemAmount()).isEqualTo(162000);
                assertThat(option.getStock().getQuantity()).isEqualTo(17);

                verify(rewardRepository, times(1)).findByIdWithOptions(reward.getId());
                verify(stockReservationRepository, times(1)).save(any(StockReservation.class));
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class Failure {

            @Test
            @DisplayName("존재하지 않는 리워드 - 예외 발생")
            void rewardNotFound() {
                // given
                UUID userId = UUID.randomUUID();
                UUID fundingId = UUID.randomUUID();
                UUID rewardId = UUID.randomUUID();
                StockReserveCommand.StockReserveItemCommand item =
                        new StockReserveCommand.StockReserveItemCommand(rewardId, null, 1);

                when(rewardRepository.findByIdWithOptions(rewardId)).thenReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> transactionService.reserveStockForItem(userId, fundingId, item))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.REWARD_NOT_FOUND);

                verify(rewardRepository, times(1)).findByIdWithOptions(rewardId);
                verify(stockReservationRepository, never()).save(any());
            }

            @Test
            @DisplayName("존재하지 않는 옵션 - 예외 발생")
            void optionNotFound() {
                // given
                UUID userId = UUID.randomUUID();
                UUID fundingId = UUID.randomUUID();
                Rewards reward = createRewardWithOptions();
                UUID invalidOptionId = UUID.randomUUID();
                StockReserveCommand.StockReserveItemCommand item =
                        new StockReserveCommand.StockReserveItemCommand(reward.getId(), invalidOptionId, 1);

                when(rewardRepository.findByIdWithOptions(reward.getId())).thenReturn(Optional.of(reward));

                // when & then
                assertThatThrownBy(() -> transactionService.reserveStockForItem(userId, fundingId, item))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.OPTION_NOT_FOUND);

                verify(rewardRepository, times(1)).findByIdWithOptions(reward.getId());
                verify(stockReservationRepository, never()).save(any());
            }

            @Test
            @DisplayName("필수 옵션 미선택 - 예외 발생")
            void requiredOptionNotSelected() {
                // given
                UUID userId = UUID.randomUUID();
                UUID fundingId = UUID.randomUUID();
                Rewards reward = createRewardWithRequiredOption();
                StockReserveCommand.StockReserveItemCommand item =
                        new StockReserveCommand.StockReserveItemCommand(reward.getId(), null, 1);

                when(rewardRepository.findByIdWithOptions(reward.getId())).thenReturn(Optional.of(reward));

                // when & then
                assertThatThrownBy(() -> transactionService.reserveStockForItem(userId, fundingId, item))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.REQUIRED_OPTION_NOT_SELECTED);

                verify(rewardRepository, times(1)).findByIdWithOptions(reward.getId());
                verify(stockReservationRepository, never()).save(any());
            }

            @Test
            @DisplayName("재고 부족 - 예외 발생 (리워드)")
            void insufficientStockReward() {
                // given
                UUID userId = UUID.randomUUID();
                UUID fundingId = UUID.randomUUID();
                Rewards reward = createRewardWithoutOption(10);
                StockReserveCommand.StockReserveItemCommand item =
                        new StockReserveCommand.StockReserveItemCommand(reward.getId(), null, 20);

                when(rewardRepository.findByIdWithOptions(reward.getId())).thenReturn(Optional.of(reward));

                // when & then
                assertThatThrownBy(() -> transactionService.reserveStockForItem(userId, fundingId, item))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.INSUFFICIENT_STOCK);

                verify(rewardRepository, times(1)).findByIdWithOptions(reward.getId());
                verify(stockReservationRepository, never()).save(any());
            }

            @Test
            @DisplayName("재고 부족 - 예외 발생 (옵션)")
            void insufficientStockOption() {
                // given
                UUID userId = UUID.randomUUID();
                UUID fundingId = UUID.randomUUID();
                Rewards reward = createRewardWithOptions();
                RewardOptions option = reward.getOptionList().get(2);
                StockReserveCommand.StockReserveItemCommand item =
                        new StockReserveCommand.StockReserveItemCommand(reward.getId(), option.getId(), 30);

                when(rewardRepository.findByIdWithOptions(reward.getId())).thenReturn(Optional.of(reward));

                // when & then
                assertThatThrownBy(() -> transactionService.reserveStockForItem(userId, fundingId, item))
                        .isInstanceOf(RewardException.class)
                        .extracting("errorCode")
                        .isEqualTo(RewardErrorCode.INSUFFICIENT_STOCK);

                verify(rewardRepository, times(1)).findByIdWithOptions(reward.getId());
                verify(stockReservationRepository, never()).save(any());
            }
        }
    }
}