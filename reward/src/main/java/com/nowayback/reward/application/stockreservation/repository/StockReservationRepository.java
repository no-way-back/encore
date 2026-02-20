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
    List<StockReservation> findExpiredPendingReservations(LocalDateTime now);
}