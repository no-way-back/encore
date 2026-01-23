package com.nowayback.reward.application.reward;

import com.nowayback.reward.application.reward.command.StockReserveCommand;
import com.nowayback.reward.application.reward.dto.StockReserveResult;
import com.nowayback.reward.application.reward.repository.RewardRepository;
import com.nowayback.reward.application.stockreservation.repository.StockReservationRepository;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.reward.entity.RewardOptions;
import com.nowayback.reward.domain.reward.entity.Rewards;
import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.nowayback.reward.domain.exception.RewardErrorCode.REWARD_NOT_FOUND;
import static com.nowayback.reward.domain.exception.RewardErrorCode.RESERVATION_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardStockTransactionService {

    private final RewardRepository rewardRepository;
    private final StockReservationRepository stockReservationRepository;

    /**
     * 재고 예약 트랜잭션 처리
     */
    @Transactional
    public StockReserveResult.ReservationWithPrice reserveStockForItem(
            UUID userId,
            UUID fundingId,
            StockReserveCommand.StockReserveItemCommand item
    ) {
        Rewards reward = findRewardByIdWithOptions(item.rewardId());

        if (item.optionId() != null) {
            return reserveWithOption(userId, fundingId, reward, item);
        }

        reward.validateRequiredOption();
        return reserveWithoutOption(userId, fundingId, reward, item);
    }

    /**
     * 재고 복원 트랜잭션 처리
     */
    @Transactional
    public void restoreStockForReservation(UUID reservationId) {
        StockReservation reservation = stockReservationRepository.findById(reservationId)
                .orElseThrow(() -> new RewardException(RESERVATION_NOT_FOUND));

        Rewards reward = findRewardByIdWithOptions(reservation.getRewardId().getId());

        if (reservation.getOptionId() != null) {
            restoreWithOption(reward, reservation);
        } else {
            restoreWithoutOption(reward, reservation);
        }

        reservation.restore();
    }

    // ========== 재고 예약 로직 ==========

    private StockReserveResult.ReservationWithPrice reserveWithOption(
            UUID userId,
            UUID fundingId,
            Rewards reward,
            StockReserveCommand.StockReserveItemCommand item
    ) {
        RewardOptions option = reward.findOption(item.optionId());
        option.decreaseStock(item.quantity());
        reward.syncStatus();

        Long itemAmount = option.calculateTotalAmount(item.quantity());
        StockReservation reservation = createReservation(userId, fundingId, reward.getId(), option.getId(), item.quantity());

        log.info("옵션 재고 예약 완료 - fundingId: {}, rewardId: {}, optionId: {}, quantity: {}",
                fundingId, reward.getId(), option.getId(), item.quantity());

        return StockReserveResult.ReservationWithPrice.of(reservation, itemAmount);
    }

    private StockReserveResult.ReservationWithPrice reserveWithoutOption(
            UUID userId,
            UUID fundingId,
            Rewards reward,
            StockReserveCommand.StockReserveItemCommand item
    ) {
        reward.decreaseStock(item.quantity());
        Long itemAmount = reward.calculateTotalAmount(item.quantity());

        StockReservation reservation = createReservation(userId, fundingId, reward.getId(), null, item.quantity());

        log.info("리워드 재고 예약 완료 - fundingId: {}, rewardId: {}, quantity: {}",
                fundingId, reward.getId(), item.quantity());

        return StockReserveResult.ReservationWithPrice.of(reservation, itemAmount);
    }

    // ========== 재고 복원 로직 ==========

    private void restoreWithOption(Rewards reward, StockReservation reservation) {
        RewardOptions option = reward.findOption(reservation.getOptionId().getId());
        option.restoreStock(reservation.getQuantity());
        reward.syncStatus();

        log.info("옵션 재고 복원 완료 - fundingId: {}, rewardId: {}, optionId: {}, quantity: {}",
                reservation.getFundingId(), reward.getId(), option.getId(), reservation.getQuantity());
    }

    private void restoreWithoutOption(Rewards reward, StockReservation reservation) {
        reward.restoreStock(reservation.getQuantity());

        log.info("리워드 재고 복원 완료 - fundingId: {}, rewardId: {}, quantity: {}",
                reservation.getFundingId(), reward.getId(), reservation.getQuantity());
    }

    // ========== 헬퍼 메서드 ==========

    private Rewards findRewardByIdWithOptions(UUID rewardId) {
        return rewardRepository.findByIdWithOptions(rewardId)
                .orElseThrow(() -> new RewardException(REWARD_NOT_FOUND));
    }

    private StockReservation createReservation(
            UUID userId,
            UUID fundingId,
            UUID rewardId,
            UUID optionId,
            Integer quantity
    ) {
        StockReservation reservation = StockReservation.create(userId, fundingId, rewardId, optionId, quantity);
        return stockReservationRepository.save(reservation);
    }
}