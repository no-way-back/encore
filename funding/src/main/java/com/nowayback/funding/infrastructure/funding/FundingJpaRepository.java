package com.nowayback.funding.infrastructure.funding;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nowayback.funding.domain.funding.entity.Funding;
import com.nowayback.funding.domain.funding.entity.FundingStatus;

public interface FundingJpaRepository extends JpaRepository<Funding, UUID> {

	Optional<Funding> findByIdempotencyKey(String idempotencyKey);

	boolean existsByUserIdAndProjectIdAndStatus(UUID userId, UUID projectId, FundingStatus status);

	@Query("SELECT f FROM Funding f " +
		"WHERE f.userId = :userId " +
		"AND (:status IS NULL OR f.status = :status) " +
		"AND (:startDate IS NULL OR f.createdAt >= :startDate)")
	Page<Funding> findMyFundings(
		@Param("userId") UUID userId,
		@Param("status") FundingStatus status,
		@Param("startDate") LocalDateTime startDate,
		Pageable pageable
	);

	@Query("SELECT f FROM Funding f " +
		"WHERE f.projectId = :projectId " +
		"AND f.status = :status " +
		"ORDER BY f.createdAt DESC")
	Page<Funding> findProjectSponsors(
		@Param("projectId") UUID projectId,
		@Param("status") FundingStatus status,
		Pageable pageable
	);

	@Query("SELECT COALESCE(SUM(f.amount), 0) FROM Funding f " +
		"WHERE f.projectId = :projectId " +
		"AND f.status = :status")
	Long sumAmountByProjectIdAndStatus(
		@Param("projectId") UUID projectId,
		@Param("status") FundingStatus status
	);
}
