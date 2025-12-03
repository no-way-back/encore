package com.nowayback.payment.infrastructure.payment.repository;

import com.nowayback.payment.domain.payment.entity.PaymentStatusLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentStatusLogJpaRepository extends JpaRepository<PaymentStatusLog, UUID> {
    Page<PaymentStatusLog> findAllByPaymentId(UUID paymentId, Pageable pageable);
}
