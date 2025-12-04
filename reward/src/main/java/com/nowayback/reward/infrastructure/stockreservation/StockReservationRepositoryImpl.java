package com.nowayback.reward.infrastructure.stockreservation;

import com.nowayback.reward.domain.repository.StockReservationRepository;
import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class StockReservationRepositoryImpl implements StockReservationRepository {

    private final StockReservationJpaRepository jpaRepository;

    @Transactional
    public StockReservation save(StockReservation stockReservation) {
        return jpaRepository.save(stockReservation);
    }
}
