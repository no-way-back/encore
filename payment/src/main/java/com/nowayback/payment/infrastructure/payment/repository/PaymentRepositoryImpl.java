package com.nowayback.payment.infrastructure.payment.repository;

import com.nowayback.payment.domain.payment.entity.Payment;
import com.nowayback.payment.domain.payment.repository.PaymentRepository;
import com.nowayback.payment.domain.payment.vo.FundingId;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;
import com.nowayback.payment.domain.payment.vo.ProjectId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(UUID paymentId) {
        return paymentJpaRepository.findById(paymentId);
    }

    @Override
    public List<Payment> findAllCompletedByProjectId(ProjectId projectId) {
        return paymentJpaRepository.findAllByProjectIdAndStatus(projectId, PaymentStatus.COMPLETED);
    }

    @Override
    public Long sumAmountByProjectId(ProjectId projectId) {
        return paymentJpaRepository.sumAmountByProjectId(projectId);
    }
}
