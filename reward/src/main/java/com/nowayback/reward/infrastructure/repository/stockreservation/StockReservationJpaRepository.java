package com.nowayback.reward.infrastructure.repository.stockreservation;

import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import com.nowayback.reward.domain.stockreservation.vo.ReservationStatus;
import com.nowayback.reward.domain.vo.FundingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface StockReservationJpaRepository extends JpaRepository<StockReservation, UUID> {
    List<StockReservation> findByFundingId(FundingId fundingId);

    List<StockReservation> findByFundingIdAndStatus(FundingId fundingId, ReservationStatus status);

    @Query("SELECT sr FROM StockReservation sr WHERE sr.status = 'PENDING' AND sr.expiresAt < :now")
    List<StockReservation> findExpiredPendingReservations(@Param("now") LocalDateTime now);
}