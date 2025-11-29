package com.nowayback.funding.infrastucture.fundingProjectStatistics;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatistics;

import jakarta.persistence.LockModeType;

@Repository
public interface FundingProjectStatisticsJpaRepository extends JpaRepository<FundingProjectStatistics, UUID> {

	Optional<FundingProjectStatistics> findByProjectId(UUID projectId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT fps FROM FundingProjectStatistics fps WHERE fps.projectId = :projectId")
	Optional<FundingProjectStatistics> findByProjectIdWithLock(@Param("projectId") UUID projectId);
}
