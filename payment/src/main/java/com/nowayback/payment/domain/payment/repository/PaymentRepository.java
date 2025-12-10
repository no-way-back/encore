package com.nowayback.payment.domain.payment.repository;

import com.nowayback.payment.domain.payment.entity.Payment;
import com.nowayback.payment.domain.payment.vo.FundingId;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;
import com.nowayback.payment.domain.payment.vo.ProjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);
    boolean existsByFundingIdAndStatus(FundingId fundingId, PaymentStatus paymentStatus);
    Optional<Payment> findById(UUID paymentId);
    Optional<Payment> findByFundingIdAndStatus(FundingId fundingId, PaymentStatus paymentStatus);
    List<Payment> findAllCompletedByProjectId(ProjectId uuid);
    Page<Payment> searchPayments(UUID userId, UUID projectId, Pageable pageable);
    Long sumAmountByProjectId(ProjectId projectId);
}
