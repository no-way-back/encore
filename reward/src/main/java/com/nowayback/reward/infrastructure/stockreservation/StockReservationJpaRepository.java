package com.nowayback.reward.infrastructure.stockreservation;

import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import com.nowayback.reward.domain.vo.FundingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StockReservationJpaRepository extends JpaRepository<StockReservation, UUID> {
    List<StockReservation> findByFundingId(FundingId fundingId);
}