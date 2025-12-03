package com.nowayback.payment.infrastructure.payment.repository;

import com.nowayback.payment.domain.payment.entity.PaymentStatusLog;
import com.nowayback.payment.domain.payment.repository.PaymentStatusLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentStatusLogRepositoryImpl implements PaymentStatusLogRepository {

    private final PaymentStatusLogJpaRepository jpaRepository;

    @Override
    public PaymentStatusLog save(PaymentStatusLog paymentStatusLog) {
        return jpaRepository.save(paymentStatusLog);
    }

    @Override
    public Page<PaymentStatusLog> findAllByPaymentId(UUID paymentId, Pageable pageable) {
        return jpaRepository.findAllByPaymentId(paymentId, pageable);
    }
}
