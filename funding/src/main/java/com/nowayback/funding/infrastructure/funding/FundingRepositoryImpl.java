package com.nowayback.funding.infrastructure.funding;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.domain.funding.entity.FundingStatus;
import com.nowayback.funding.domain.funding.repository.FundingRepository;

@Repository
public class FundingRepositoryImpl implements FundingRepository {

	private final FundingJpaRepository fundingJpaRepository;

	public FundingRepositoryImpl(FundingJpaRepository fundingJpaRepository) {
		this.fundingJpaRepository = fundingJpaRepository;
	}

	@Override
	public Funding save(Funding funding) {
		return fundingJpaRepository.save(funding);
	}

	@Override
	public Optional<Funding> findById(UUID id) {
		return fundingJpaRepository.findById(id);
	}

	@Override
	public Optional<Funding> findByIdempotencyKey(String idempotencyKey) {
		return fundingJpaRepository.findByIdempotencyKey(idempotencyKey);
	}

	@Override
	public boolean existsByUserIdAndProjectIdAndStatus(UUID userId, UUID projectId, FundingStatus status) {
		return fundingJpaRepository.existsByUserIdAndProjectIdAndStatus(userId, projectId, status);
	}
}
