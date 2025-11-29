package com.nowayback.funding.domain.funding.repository;

import java.util.Optional;
import java.util.UUID;

import com.nowayback.funding.domain.funding.entity.Funding;

public interface FundingRepository {

	Funding save(Funding funding);

	Optional<Funding> findById(UUID id);

	Optional<Funding> findByIdempotencyKey(String idempotencyKey);
}
