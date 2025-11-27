package com.nowayback.funding.domain.fundingProjectStatistics.entity;

import java.time.LocalDateTime;
import java.util.UUID;

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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "funding_project_statistics)")
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
}
