package com.nowayback.payment.domain.jobs.repository;

import com.nowayback.payment.domain.jobs.entity.RefundJob;
import com.nowayback.payment.domain.jobs.vo.RefundJobStatus;

import java.util.List;

public interface RefundJobRepository {
    RefundJob save(RefundJob refundJob);
    void saveAll(List<RefundJob> refundJobs);
    List<RefundJob> findTop100ByStatusOrderByCreatedAt(RefundJobStatus status);
}
