package com.nowayback.funding.infrastucture.funding;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nowayback.funding.domain.funding.entity.Funding;

public interface FundingJpaRepository extends JpaRepository<Funding, UUID> {

	Optional<Funding> findByIdempotencyKey(String idempotencyKey);
}
