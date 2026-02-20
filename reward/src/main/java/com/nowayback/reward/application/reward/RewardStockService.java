package com.nowayback.reward.application.reward;

import com.nowayback.reward.application.reward.command.StockReserveCommand;
import com.nowayback.reward.application.reward.dto.StockReserveResult;
import com.nowayback.reward.application.stockreservation.repository.StockReservationRepository;
import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import com.nowayback.reward.domain.vo.FundingId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.nowayback.reward.domain.exception.RewardErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardStockService {

    private final StockReservationRepository stockReservationRepository;
    private final RedissonClient redissonClient;
    private final RewardStockTransactionService transactionService;

    /**
     * 재고 예약 (차감)
     */
    public StockReserveResult reserveStock(StockReserveCommand command) {
        log.info("재고 예약 시작 - userId: {}, fundingId: {}, items: {}개",
                command.userId(), command.fundingId(), command.items().size());

        List<StockReserveResult.ReservationWithPrice> reservations = command.items().stream()
                .map(item -> executeWithLock(
                        "reward:stock:" + item.rewardId(),
                        () -> transactionService.reserveStockForItem(command.userId(), command.fundingId(), item)
                ))
                .toList();

        StockReserveResult result = StockReserveResult.from(command.fundingId(), reservations);

        log.info("재고 예약 완료 - userId: {}, fundingId: {}, 예약: {}개, 총액: {}",
                command.userId(), command.fundingId(), reservations.size(), result.totalAmount());

        return result;
    }

    /**
     * 재고 복원 (증가)
     */
    public void restoreStock(UUID fundingId) {
        log.info("재고 복원 시작 - fundingId: {}", fundingId);

        List<StockReservation> reservations = stockReservationRepository
                .findByFundingId(FundingId.of(fundingId));

        if (reservations.isEmpty()) {
            log.warn("펀딩에 해당하는 예약이 없습니다 - fundingId: {}", fundingId);
            return;
        }

        log.info("복원 대상 예약 수: {}", reservations.size());

        reservations.forEach(reservation -> {
            if (!reservation.isRestored()) {
                executeWithLock(
                        "reward:stock:" + reservation.getRewardId().getId(),
                        () -> {
                            transactionService.restoreStockForReservation(reservation);
                            return null;
                        }
                );
            } else {
                log.warn("이미 복원된 예약 건너뜀 - reservationId: {}", reservation.getId());
            }
        });

        log.info("재고 복원 완료 - fundingId: {}, 처리된 예약 수: {}", fundingId, reservations.size());
    }

    /**
     * 분산 락을 사용하여 작업 실행
     * - 템플릿 메서드 패턴으로 중복 제거
     */
    private <T> T executeWithLock(String lockKey, Supplier<T> action) {
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(15, 10, TimeUnit.SECONDS);
            if (!acquired) {
                log.error("락 획득 실패 - lockKey: {}", lockKey);
                throw new RewardException(STOCK_LOCK_TIMEOUT);
            }

            log.debug("분산 락 획득 성공 - lockKey: {}", lockKey);

            return action.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("락 획득 중 인터럽트 발생 - lockKey: {}", lockKey, e);
            throw new RewardException(STOCK_LOCK_INTERRUPTED);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("분산 락 해제 완료 - lockKey: {}", lockKey);
            }
        }
    }
}