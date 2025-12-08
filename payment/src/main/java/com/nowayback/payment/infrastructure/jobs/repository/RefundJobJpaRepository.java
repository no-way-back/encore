package com.nowayback.payment.infrastructure.jobs.repository;

import com.nowayback.payment.domain.jobs.entity.RefundJob;
import com.nowayback.payment.domain.jobs.vo.RefundJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefundJobJpaRepository extends JpaRepository<RefundJob, Long> {
    List<RefundJob> findTop100ByStatusOrderByCreatedAt(RefundJobStatus status);
}
