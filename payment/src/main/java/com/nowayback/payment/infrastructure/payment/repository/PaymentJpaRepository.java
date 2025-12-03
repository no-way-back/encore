package com.nowayback.payment.infrastructure.payment.repository;

import com.nowayback.payment.domain.payment.entity.Payment;
import com.nowayback.payment.domain.payment.vo.FundingId;
import com.nowayback.payment.domain.payment.vo.PaymentStatus;
import com.nowayback.payment.domain.payment.vo.ProjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByFundingId(FundingId fundingId);

    @Query("SELECT SUM(p.amount.amount) FROM Payment p WHERE p.projectId = :projectId AND p.status = 'COMPLETED'")
    Long sumAmountByProjectId(@Param("projectId") ProjectId projectId);

    List<Payment> findAllByProjectIdAndStatus(ProjectId uuid, PaymentStatus paymentStatus);
}
