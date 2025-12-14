package com.nowayback.payment.infrastructure.jobs.repository;

import com.nowayback.payment.domain.jobs.entity.RefundJob;
import com.nowayback.payment.domain.jobs.repository.RefundJobRepository;
import com.nowayback.payment.domain.jobs.vo.RefundJobStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RefundJobRepositoryImpl implements RefundJobRepository {

    private final RefundJobJpaRepository jpaRepository;

    @Override
    public RefundJob save(RefundJob refundJob) {
        return jpaRepository.save(refundJob);
    }

    @Override
    public void saveAll(List<RefundJob> refundJobs) {
        jpaRepository.saveAll(refundJobs);
    }

    @Override
    public List<RefundJob> findTop100ByStatusOrderByCreatedAt(RefundJobStatus status) {
        return jpaRepository.findTop100ByStatusOrderByCreatedAt(status);
    }
}
