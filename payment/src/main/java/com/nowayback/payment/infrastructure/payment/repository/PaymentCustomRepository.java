package com.nowayback.payment.infrastructure.payment.repository;

import com.nowayback.payment.domain.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentCustomRepository {
    Page<Payment> searchPayments(UUID userId, UUID projectId, Pageable pageable);
}
