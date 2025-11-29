package com.nowayback.funding.domain.funding.entity;

public enum FundingStatus {
	PENDING,        // 처리 중
	COMPLETED,      // 완료
	FAILED,         // 실패 (이력 남김)
	CANCELLED,      // 취소
	REFUNDED        // 환불 완료
}
