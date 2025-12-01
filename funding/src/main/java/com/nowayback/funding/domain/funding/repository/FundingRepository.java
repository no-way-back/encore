package com.nowayback.funding.domain.funding.repository;

import java.util.Optional;
import java.util.UUID;

import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.domain.funding.entity.FundingStatus;

public interface FundingRepository {

	Funding save(Funding funding);

	Optional<Funding> findById(UUID id);

	Optional<Funding> findByIdempotencyKey(String idempotencyKey);

	boolean existsByUserIdAndProjectIdAndStatus(UUID userId, UUID projectId, FundingStatus status);
}
