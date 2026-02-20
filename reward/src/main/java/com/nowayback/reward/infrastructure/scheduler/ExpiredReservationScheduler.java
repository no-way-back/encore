package com.nowayback.reward.infrastructure.scheduler;

import com.nowayback.reward.application.reward.RewardStockService;
import com.nowayback.reward.application.stockreservation.repository.StockReservationRepository;
import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiredReservationScheduler {

    private final StockReservationRepository stockReservationRepository;
    private final RewardStockService rewardStockService;

    /**
     * 매 1분마다 만료된 PENDING 예약을 찾아서 재고 복원
     */
    @Scheduled(cron = "0 * * * * *")  // 매분 실행
    public void restoreExpiredReservations() {
        log.info("만료된 PENDING 예약 복원 스케줄러 시작");

        List<StockReservation> expiredReservations = stockReservationRepository
                .findExpiredPendingReservations(LocalDateTime.now());

        if (expiredReservations.isEmpty()) {
            log.debug("만료된 PENDING 예약 없음");
            return;
        }

        log.warn("만료된 PENDING 예약 발견 - 복원 시작: {}개", expiredReservations.size());

        expiredReservations.forEach(reservation -> {
            try {
                rewardStockService.restoreStock(reservation.getFundingId().getId());
                log.info("만료된 예약 복원 완료 - fundingId: {}, reservationId: {}",
                        reservation.getFundingId(), reservation.getId());
            } catch (Exception e) {
                log.error("만료된 예약 복원 실패 - reservationId: {}", reservation.getId(), e);
            }
        });

        log.info("만료된 PENDING 예약 복원 스케줄러 완료 - 처리: {}개", expiredReservations.size());
    }
}