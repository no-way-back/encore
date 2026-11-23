package com.nowayback.reward.infrastructure.repository.stockreservation;

import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import com.nowayback.reward.domain.stockreservation.vo.ReservationStatus;
import com.nowayback.reward.domain.vo.FundingId;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockReservationJpaRepository extends JpaRepository<StockReservation, UUID> {
    List<StockReservation> findByFundingId(FundingId fundingId);

    List<StockReservation> findByFundingIdAndStatus(FundingId fundingId, ReservationStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT sr FROM StockReservation sr WHERE sr.id = :id")
    Optional<StockReservation> findByIdForUpdate(@Param("id") UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT sr FROM StockReservation sr WHERE sr.fundingId = :fundingId")
    List<StockReservation> findByFundingIdForUpdate(@Param("fundingId") FundingId fundingId);

    @Query("SELECT sr FROM StockReservation sr WHERE sr.status = :status AND sr.expiresAt < :now ORDER BY sr.expiresAt")
    List<StockReservation> findByStatusAndExpiresAtBefore(
            @Param("status") ReservationStatus status,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );
}