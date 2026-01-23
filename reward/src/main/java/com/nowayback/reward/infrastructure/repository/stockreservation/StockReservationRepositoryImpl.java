package com.nowayback.reward.infrastructure.repository.stockreservation;

import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import com.nowayback.reward.application.stockreservation.repository.StockReservationRepository;
import com.nowayback.reward.domain.stockreservation.vo.ReservationStatus;
import com.nowayback.reward.domain.vo.FundingId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StockReservationRepositoryImpl implements StockReservationRepository {

    private final StockReservationJpaRepository jpaRepository;

    @Transactional
    public StockReservation save(StockReservation stockReservation) {
        return jpaRepository.save(stockReservation);
    }

    @Override
    public Optional<StockReservation> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<StockReservation> findByFundingId(FundingId fundingId) {
        return jpaRepository.findByFundingId(fundingId);
    }

    @Override
    public List<StockReservation> findByFundingIdAndStatus(FundingId fundingId, ReservationStatus status) {
        return jpaRepository.findByFundingIdAndStatus(fundingId, status);
    }

    @Override
    public List<StockReservation> findExpiredPendingReservations(LocalDateTime now) {
        return jpaRepository.findExpiredPendingReservations(now);
    }
}