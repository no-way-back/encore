package com.nowayback.reward.domain.repository;

import com.nowayback.reward.domain.stockreservation.entity.StockReservation;

public interface StockReservationRepository {
    StockReservation save(StockReservation stockReservation);
}
