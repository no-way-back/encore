package com.nowayback.funding.domain.queue.repository;

import com.nowayback.funding.domain.queue.vo.PendingFunding;

import java.util.Optional;
import java.util.UUID;

/**
 * 대기 중인 펀딩 데이터 Repository
 */
public interface PendingFundingRepository {

    /**
     * 대기 펀딩 저장
     *
     * @param pendingFunding 대기 펀딩
     */
    void save(PendingFunding pendingFunding);

    /**
     * 대기 펀딩 조회
     *
     * @param userId 사용자 ID
     * @return 대기 펀딩
     */
    Optional<PendingFunding> findByUserId(UUID userId);

    /**
     * 대기 펀딩 삭제
     *
     * @param userId 사용자 ID
     */
    void deleteByUserId(UUID userId);
}