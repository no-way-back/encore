package com.nowayback.payment.domain.payment.repository;

import com.nowayback.payment.domain.payment.entity.Payment;
import com.nowayback.payment.domain.payment.vo.ProjectId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(UUID paymentId);
    List<Payment> findAllCompletedByProjectId(ProjectId uuid);

    Long sumAmountByProjectId(ProjectId projectId);
}
