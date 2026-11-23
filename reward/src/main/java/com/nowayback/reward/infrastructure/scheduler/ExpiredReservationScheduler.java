package com.nowayback.reward.infrastructure.scheduler;

import com.nowayback.reward.application.reward.RewardStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiredReservationScheduler {

    private final RewardStockService rewardStockService;

    /**
     * 매 1분마다 만료된 PENDING 예약의 재고를 복원한다
     */
    @Scheduled(cron = "0 * * * * *")
    public void restoreExpiredReservations() {
        int restored = rewardStockService.restoreExpiredReservations();
        if (restored > 0) {
            log.warn("만료된 PENDING 예약 복원 - {}개", restored);
        }
    }
}
