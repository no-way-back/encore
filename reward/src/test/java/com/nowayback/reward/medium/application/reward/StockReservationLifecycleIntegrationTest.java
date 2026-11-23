package com.nowayback.reward.medium.application.reward;

import com.nowayback.reward.application.reward.RewardStockService;
import com.nowayback.reward.application.reward.command.StockReserveCommand;
import com.nowayback.reward.application.reward.dto.StockReserveResult;
import com.nowayback.reward.application.reward.repository.RewardRepository;
import com.nowayback.reward.application.stockreservation.repository.StockReservationRepository;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.reward.command.CreateRewardCommand;
import com.nowayback.reward.domain.reward.entity.Rewards;
import com.nowayback.reward.domain.reward.vo.RewardType;
import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import com.nowayback.reward.domain.stockreservation.vo.ReservationStatus;
import com.nowayback.reward.domain.vo.FundingId;
import org.junit.jupiter.api.BeforeEach;
import com.nowayback.reward.medium.PostgresIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 재고 예약의 임시예약 → 확정/복원 흐름을 실제 PostgreSQL에서 확인한다.
 * (분산 락은 Redis 없이 항상 획득되도록 mock)
 */
class StockReservationLifecycleIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private RewardStockService rewardStockService;

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private StockReservationRepository stockReservationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void alwaysAcquireLock() throws InterruptedException {
        RLock lock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(true);
    }

    private Rewards saveReward(int stock) {
        CreateRewardCommand command = new CreateRewardCommand(
                UUID.randomUUID(), UUID.randomUUID(), "티셔츠", "설명",
                30000L, stock, 3000, 50000, 10, RewardType.GENERAL, null
        );
        return rewardRepository.save(Rewards.create(command));
    }

    private int stockOf(UUID rewardId) {
        return rewardRepository.findByIdWithOptions(rewardId).orElseThrow().getStock().getQuantity();
    }

    private List<StockReservation> reservationsOf(UUID fundingId) {
        return stockReservationRepository.findByFundingId(FundingId.of(fundingId));
    }

    private StockReserveResult reserve(UUID fundingId, UUID rewardId, int quantity) {
        return rewardStockService.reserveStock(new StockReserveCommand(
                UUID.randomUUID(), fundingId,
                List.of(new StockReserveCommand.StockReserveItemCommand(rewardId, null, quantity))
        ));
    }

    private void expire(UUID fundingId) {
        jdbcTemplate.update(
                "UPDATE p_stock_reservations SET expires_at = now() - interval '1 minute' WHERE funding_id = ?",
                fundingId
        );
    }

    @Test
    @DisplayName("예약하면 재고가 차감되고 PENDING 상태로 만료 시각이 생긴다")
    void reserve_pending() {
        Rewards reward = saveReward(10);
        UUID fundingId = UUID.randomUUID();

        reserve(fundingId, reward.getId(), 3);

        assertThat(stockOf(reward.getId())).isEqualTo(7);
        StockReservation reservation = reservationsOf(fundingId).get(0);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(reservation.getExpiresAt()).isNotNull();
    }

    @Test
    @DisplayName("결제 성공으로 확정된 예약은 만료 시각이 지나도 복원되지 않는다")
    void confirmed_notRestoredByExpiry() {
        Rewards reward = saveReward(10);
        UUID fundingId = UUID.randomUUID();
        reserve(fundingId, reward.getId(), 3);

        rewardStockService.confirmReservations(fundingId);
        expire(fundingId);
        rewardStockService.restoreExpiredReservations();

        assertThat(reservationsOf(fundingId).get(0).getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(stockOf(reward.getId())).isEqualTo(7);
    }

    @Test
    @DisplayName("결제가 확정되지 않은 채 만료되면 재고가 복원된다")
    void expiredPending_restored() {
        Rewards reward = saveReward(10);
        UUID fundingId = UUID.randomUUID();
        reserve(fundingId, reward.getId(), 3);

        expire(fundingId);
        rewardStockService.restoreExpiredReservations();

        assertThat(reservationsOf(fundingId).get(0).getStatus()).isEqualTo(ReservationStatus.RESTORED);
        assertThat(stockOf(reward.getId())).isEqualTo(10);
    }

    @Test
    @DisplayName("만료 전의 PENDING 예약은 스케줄러가 건드리지 않는다")
    void notExpiredPending_untouched() {
        Rewards reward = saveReward(10);
        UUID fundingId = UUID.randomUUID();
        reserve(fundingId, reward.getId(), 3);

        rewardStockService.restoreExpiredReservations();

        assertThat(reservationsOf(fundingId).get(0).getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(stockOf(reward.getId())).isEqualTo(7);
    }

    @Test
    @DisplayName("결제 실패 이벤트가 두 번 와도 재고는 한 번만 복원된다")
    void restoreTwice_restoredOnce() {
        Rewards reward = saveReward(10);
        UUID fundingId = UUID.randomUUID();
        reserve(fundingId, reward.getId(), 3);

        rewardStockService.restoreStock(fundingId);
        rewardStockService.restoreStock(fundingId);

        assertThat(stockOf(reward.getId())).isEqualTo(10);
        assertThat(reservationsOf(fundingId).get(0).getStatus()).isEqualTo(ReservationStatus.RESTORED);
    }

    @Test
    @DisplayName("확정된 예약도 환불되면 재고가 복원된다")
    void refundAfterConfirm_restored() {
        Rewards reward = saveReward(10);
        UUID fundingId = UUID.randomUUID();
        reserve(fundingId, reward.getId(), 3);
        rewardStockService.confirmReservations(fundingId);

        rewardStockService.restoreStock(fundingId);

        assertThat(stockOf(reward.getId())).isEqualTo(10);
    }

    @Test
    @DisplayName("만료로 복원된 뒤 결제 성공이 늦게 와도 재고를 다시 차감하지 않는다")
    void confirmAfterExpiry_keepsRestored() {
        Rewards reward = saveReward(10);
        UUID fundingId = UUID.randomUUID();
        reserve(fundingId, reward.getId(), 3);
        expire(fundingId);
        rewardStockService.restoreExpiredReservations();

        rewardStockService.confirmReservations(fundingId);

        assertThat(reservationsOf(fundingId).get(0).getStatus()).isEqualTo(ReservationStatus.RESTORED);
        assertThat(stockOf(reward.getId())).isEqualTo(10);
    }

    @Test
    @DisplayName("여러 리워드 중 하나가 재고 부족이면 앞서 차감한 재고도 되돌린다")
    void partialFailure_compensated() {
        Rewards enough = saveReward(10);
        Rewards scarce = saveReward(1);
        UUID fundingId = UUID.randomUUID();

        assertThatThrownBy(() -> rewardStockService.reserveStock(new StockReserveCommand(
                UUID.randomUUID(), fundingId,
                List.of(
                        new StockReserveCommand.StockReserveItemCommand(enough.getId(), null, 3),
                        new StockReserveCommand.StockReserveItemCommand(scarce.getId(), null, 5)
                )
        ))).isInstanceOf(RewardException.class);

        assertThat(stockOf(enough.getId())).isEqualTo(10);
        assertThat(stockOf(scarce.getId())).isEqualTo(1);
        assertThat(reservationsOf(fundingId))
                .extracting(StockReservation::getStatus)
                .containsOnly(ReservationStatus.RESTORED);
    }
}
