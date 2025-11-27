package com.nowayback.funding.domain.funding.entity;

public enum OutboxStatus {
	PENDING,     // 발행 대기
	PUBLISHED,   // 발행 완료
	FAILED       // 발행 실패
}
