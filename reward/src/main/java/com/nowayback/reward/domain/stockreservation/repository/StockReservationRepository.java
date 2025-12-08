package com.nowayback.reward.domain.stockreservation.repository;

import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import com.nowayback.reward.domain.vo.FundingId;

import java.util.List;

public interface StockReservationRepository {
    StockReservation save(StockReservation stockReservation);
    List<StockReservation> findByFundingId(FundingId fundingId);
}
