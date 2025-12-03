package com.nowayback.reward.domain.repository;

import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import com.nowayback.reward.domain.vo.FundingId;

import java.util.List;
import java.util.UUID;

public interface StockReservationRepository {
    StockReservation save(StockReservation stockReservation);
    List<StockReservation> findByFundingId(FundingId fundingId);
}
