package com.nowayback.funding.infrastructure.fundingProjectStatistics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatistics;
import com.nowayback.funding.domain.fundingProjectStatistics.entity.FundingProjectStatus;

import jakarta.persistence.LockModeType;

@Repository
public interface FundingProjectStatisticsJpaRepository extends JpaRepository<FundingProjectStatistics, UUID> {

	Optional<FundingProjectStatistics> findByProjectId(UUID projectId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT fps FROM FundingProjectStatistics fps WHERE fps.projectId = :projectId")
	Optional<FundingProjectStatistics> findByProjectIdWithLock(@Param("projectId") UUID projectId);

	@Query("SELECT fps FROM FundingProjectStatistics fps " +
		   "WHERE fps.status = :status AND fps.startDate <= :now")
	List<FundingProjectStatistics> findByStatusAndStartDateBefore(
		@Param("status") FundingProjectStatus status,
		@Param("now") LocalDateTime now
	);

	@Query("SELECT fps FROM FundingProjectStatistics fps " +
	       "WHERE fps.status = :status AND fps.endDate <= :now")
	List<FundingProjectStatistics> findByStatusAndEndDateBefore(
		@Param("status") FundingProjectStatus status,
		@Param("now") LocalDateTime now
	);
}
