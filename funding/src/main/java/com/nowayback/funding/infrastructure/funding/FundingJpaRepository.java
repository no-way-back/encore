package com.nowayback.funding.infrastructure.funding;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.domain.funding.entity.FundingStatus;

public interface FundingJpaRepository extends JpaRepository<Funding, UUID> {

	Optional<Funding> findByIdempotencyKey(String idempotencyKey);

	boolean existsByUserIdAndProjectIdAndStatus(UUID userId, UUID projectId, FundingStatus status);
}
