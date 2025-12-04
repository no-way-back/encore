package com.nowayback.reward.infrastructure.stockreservation;

import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StockReservationJpaRepository extends JpaRepository<StockReservation, UUID> {
}
