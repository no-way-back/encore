package com.nowayback.funding.domain.fundingProjectStatistics.entity;

public enum FundingProjectStatus {
	SCHEDULED,              // 진행 전
	PROCESSING,				// 진행 중
	SUCCESS,                // 성공
	REFUND_IN_PROGRESS,     // 환불 진행 중
	SETTLEMENT_IN_PROGRESS, // 정산 중
	FAILED                  // 실패
}
