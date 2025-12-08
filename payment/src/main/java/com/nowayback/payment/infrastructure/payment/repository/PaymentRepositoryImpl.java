package com.nowayback.payment.infrastructure.payment.repository;

import com.nowayback.payment.domain.payment.entity.Payment;
import com.nowayback.payment.domain.payment.repository.PaymentRepository;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;
import com.nowayback.payment.domain.payment.vo.ProjectId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;
    private final PaymentCustomRepository customRepository;

    @Override
    public Payment save(Payment payment) {
        return jpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(UUID paymentId) {
        return jpaRepository.findById(paymentId);
    }

    @Override
    public Page<Payment> searchPayments(UUID userId, UUID projectId, Pageable pageable) {
        return customRepository.searchPayments(userId, projectId, pageable);
    }

    @Override
    public List<Payment> findAllCompletedByProjectId(ProjectId projectId) {
        return jpaRepository.findAllByProjectIdAndStatus(projectId, PaymentStatus.COMPLETED);
    }

    @Override
    public Long sumAmountByProjectId(ProjectId projectId) {
        return jpaRepository.sumAmountByProjectId(projectId);
    }
}
