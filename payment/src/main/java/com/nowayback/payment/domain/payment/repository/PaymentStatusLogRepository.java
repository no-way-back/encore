package com.nowayback.payment.domain.payment.repository;

import com.nowayback.payment.domain.payment.entity.PaymentStatusLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentStatusLogRepository {
    PaymentStatusLog save(PaymentStatusLog paymentStatusLog);
    Page<PaymentStatusLog> findAllByPaymentId(UUID paymentId, Pageable pageable);
}
