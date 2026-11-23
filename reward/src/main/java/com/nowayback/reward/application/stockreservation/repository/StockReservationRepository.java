package com.nowayback.reward.application.stockreservation.repository;

import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import com.nowayback.reward.domain.stockreservation.vo.ReservationStatus;
import com.nowayback.reward.domain.vo.FundingId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockReservationRepository {
    StockReservation save(StockReservation stockReservation);
    Optional<StockReservation> findById(UUID id);
    List<StockReservation> findByFundingId(FundingId fundingId);
    List<StockReservation> findByFundingIdAndStatus(FundingId fundingId, ReservationStatus status);

    /** 예약 행을 잠그고 조회한다 (확정과 만료 복원이 동시에 일어나지 않도록) */
    Optional<StockReservation> findByIdForUpdate(UUID id);
    List<StockReservation> findByFundingIdForUpdate(FundingId fundingId);

    /** 만료 시각이 지난 PENDING 예약 (오래된 순, 최대 limit건) */
    List<StockReservation> findExpiredPending(LocalDateTime now, int limit);
}