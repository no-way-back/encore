package com.nowayback.funding.domain.fundingProjectStatistics.entity;

import static com.nowayback.funding.domain.exception.FundingErrorCode.*;

import java.time.LocalDateTime;
import java.util.UUID;

import com.nowayback.funding.domain.exception.FundingException;
import com.nowayback.funding.domain.shared.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "funding_project_statistics)")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FundingProjectStatistics extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private UUID id;

	@Column(name = "project_id", nullable = false, unique = true)
	private UUID projectId;

	@Column(name = "target_amount", nullable = false)
	private Long targetAmount;

	@Column(name = "current_amount", nullable = false)
	private Long currentAmount;

	@Column(name = "participant_count", nullable = false)
	private Integer participantCount;

	@Column(name = "start_date", nullable = false)
	private LocalDateTime startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDateTime endDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	private FundingProjectStatus status;

	public void increaseFunding(Long amount) {
		if (amount == null || amount <= 0) {
			throw new FundingException(INVALID_AMOUNT);
		}
		this.currentAmount += amount;
		this.participantCount += 1;
	}

	public static FundingProjectStatistics create(
		UUID projectId,
		Long targetAmount,
		LocalDateTime startDate,
		LocalDateTime endDate
	) {
		FundingProjectStatistics statistics = new FundingProjectStatistics();
		statistics.projectId = projectId;
		statistics.targetAmount = targetAmount;
		statistics.currentAmount = 0L;
		statistics.participantCount = 0;
		statistics.startDate = startDate;
		statistics.endDate = endDate;
		statistics.status = determineInitialStatus(startDate);
		return statistics;
	}

	private static FundingProjectStatus determineInitialStatus(LocalDateTime startDate) {
		return LocalDateTime.now().isBefore(startDate)
			? FundingProjectStatus.SCHEDULED
			: FundingProjectStatus.PROCESSING;
	}

	/**
	 * 후원 취소 시 금액과 참여자 수 감소
	 */
	public void decreaseFunding(Long amount) {
		if (amount == null || amount <= 0) {
			throw new FundingException(INVALID_AMOUNT);
		}
		if (this.currentAmount < amount) {
			throw new FundingException(INSUFFICIENT_CURRENT_AMOUNT);
		}
		if (this.participantCount <= 0) {
			throw new FundingException(NO_PARTICIPANTS_TO_DECREASE);
		}
		this.currentAmount -= amount;
		this.participantCount -= 1;
	}

	public boolean isTargetAchieved() {
		return this.currentAmount >= this.targetAmount;
	}

	public double getAchievementRate() {
		if (this.targetAmount == 0) {
			return 0.0;
		}
		return (double) this.currentAmount / this.targetAmount * 100;
	}
}
