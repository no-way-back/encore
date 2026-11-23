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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private static final int EXPIRED_BATCH_SIZE = 100;

    /**
     * 재고 임시 예약 (PENDING)
     * - 재고를 차감하고 만료 시각이 있는 예약을 만든다
     * - 여러 리워드 중 하나라도 실패하면 앞서 예약한 것을 즉시 복원한다 (보상 트랜잭션)
     */
    public StockReserveResult reserveStock(StockReserveCommand command) {
        log.info("재고 예약 시작 - userId: {}, fundingId: {}, items: {}개",
                command.userId(), command.fundingId(), command.items().size());

        List<StockReserveResult.ReservationWithPrice> reservations = new ArrayList<>();

        try {
            for (var item : command.items()) {
                reservations.add(executeWithLock(
                        lockKey(item.rewardId()),
                        () -> transactionService.reserveStockForItem(command.userId(), command.fundingId(), item)
                ));
            }
        } catch (RuntimeException e) {
            log.error("재고 예약 실패, 보상 트랜잭션 시작 - fundingId: {}, 이미 예약된 항목: {}개",
                    command.fundingId(), reservations.size(), e);
            compensate(reservations);
            throw e;
        }

        StockReserveResult result = StockReserveResult.from(command.fundingId(), reservations);

        log.info("재고 임시 예약 완료 (PENDING) - userId: {}, fundingId: {}, 예약: {}개, 총액: {}",
                command.userId(), command.fundingId(), reservations.size(), result.totalAmount());

        return result;
    }

    /**
     * 재고 예약 확정 (결제 성공 이벤트)
     * - PENDING → CONFIRMED, 이미 확정된 예약은 그대로 둔다
     * - 예약 행을 잠가 만료 스케줄러와 동시에 처리되지 않게 한다
     */
    @Transactional
    public void confirmReservations(UUID fundingId) {
        List<StockReservation> reservations = stockReservationRepository
                .findByFundingIdForUpdate(FundingId.of(fundingId));

        if (reservations.isEmpty()) {
            log.warn("확정할 예약이 없습니다 - fundingId: {}", fundingId);
            return;
        }

        for (StockReservation reservation : reservations) {
            if (reservation.isRestored()) {
                // 결제는 성공했지만 만료로 재고가 이미 풀린 경우: 자동으로 다시 차감하면 초과 판매가 날 수 있어 운영 확인이 필요하다
                log.error("결제 성공 전에 만료·복원된 예약 - 확인 필요 - fundingId: {}, reservationId: {}",
                        fundingId, reservation.getId());
                continue;
            }
            reservation.confirm();
        }

        log.info("재고 예약 확정 완료 - fundingId: {}, 예약: {}개", fundingId, reservations.size());
    }

    /**
     * 재고 복원 (결제 실패·환불 이벤트)
     * - 이미 복원된 예약은 건너뛰므로 같은 이벤트가 다시 와도 안전하다
     */
    public void restoreStock(UUID fundingId) {
        List<StockReservation> reservations = stockReservationRepository
                .findByFundingId(FundingId.of(fundingId));

        if (reservations.isEmpty()) {
            log.warn("펀딩에 해당하는 예약이 없습니다 - fundingId: {}", fundingId);
            return;
        }

        int restored = 0;
        for (StockReservation reservation : reservations) {
            if (reservation.isRestored()) {
                continue;
            }
            boolean done = executeWithLock(
                    lockKey(reservation.getRewardId().getId()),
                    () -> transactionService.restoreStockForReservation(reservation.getId())
            );
            if (done) {
                restored++;
            }
        }

        log.info("재고 복원 완료 - fundingId: {}, 복원: {}개", fundingId, restored);
    }

    /**
     * 만료된 PENDING 예약 복원 (만료 스케줄러)
     * - 결제가 확정되지 않은 채 남은 재고를 되돌리는 마지막 안전망
     *
     * @return 복원한 예약 수
     */
    public int restoreExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<StockReservation> expired = stockReservationRepository.findExpiredPending(now, EXPIRED_BATCH_SIZE);

        int restored = 0;
        for (StockReservation reservation : expired) {
            try {
                boolean done = executeWithLock(
                        lockKey(reservation.getRewardId().getId()),
                        () -> transactionService.restoreIfExpired(reservation.getId(), now)
                );
                if (done) {
                    restored++;
                }
            } catch (RuntimeException e) {
                log.error("만료 예약 복원 실패, 다음 실행 때 다시 시도 - reservationId: {}", reservation.getId(), e);
            }
        }
        return restored;
    }

    private void compensate(List<StockReserveResult.ReservationWithPrice> reservations) {
        for (var item : reservations) {
            StockReservation reservation = item.reservation();
            try {
                executeWithLock(
                        lockKey(reservation.getRewardId().getId()),
                        () -> transactionService.restoreStockForReservation(reservation.getId())
                );
            } catch (RuntimeException e) {
                log.error("보상 트랜잭션 실패, 만료 스케줄러가 복원 예정 - reservationId: {}", reservation.getId(), e);
            }
        }
    }

    private String lockKey(UUID rewardId) {
        return "reward:stock:" + rewardId;
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