package com.nowayback.reward.infrastructure.repository.stockreservation;

import com.nowayback.reward.domain.stockreservation.entity.StockReservation;
import com.nowayback.reward.domain.stockreservation.repository.StockReservationRepository;
import com.nowayback.reward.domain.vo.FundingId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StockReservationRepositoryImpl implements StockReservationRepository {

    private final StockReservationJpaRepository jpaRepository;

    @Transactional
    public StockReservation save(StockReservation stockReservation) {
        return jpaRepository.save(stockReservation);
    }

    @Override
    public List<StockReservation> findByFundingId(FundingId fundingId) {
        return jpaRepository.findByFundingId(fundingId);
    }
}
