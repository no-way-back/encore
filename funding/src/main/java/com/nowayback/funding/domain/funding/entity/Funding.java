package com.nowayback.funding.domain.funding.entity;

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
@Table(name = "p_fundings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Funding extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "project_id", nullable = false)
	private UUID projectId;

	@Column(name = "amount", nullable = false)
	private Long amount;

	@Column(name = "includes_reward", nullable = false)
	private Boolean includesReward;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private FundingStatus status;

	@Column(name = "payment_id")
	private Long paymentId;

	@Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
	private String idempotencyKey;
}
