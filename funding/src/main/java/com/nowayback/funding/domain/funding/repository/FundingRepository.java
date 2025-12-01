package com.nowayback.funding.domain.funding.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.domain.funding.entity.FundingStatus;

public interface FundingRepository {

	Funding save(Funding funding);

	Optional<Funding> findById(UUID id);

	Optional<Funding> findByIdempotencyKey(String idempotencyKey);

	boolean existsByUserIdAndProjectIdAndStatus(UUID userId, UUID projectId, FundingStatus status);

	Page<Funding> findMyFundings(UUID userId, FundingStatus status, LocalDateTime startDate, Pageable pageable);

	Page<Funding> findProjectSponsors(UUID projectId, FundingStatus status, Pageable pageable);

	Long sumAmountByProjectIdAndStatus(UUID projectId, FundingStatus status);
}
