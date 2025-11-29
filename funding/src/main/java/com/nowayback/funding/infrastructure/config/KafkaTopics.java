package com.nowayback.funding.infrastructure.config;

public final class KafkaTopics {

	private KafkaTopics() {
		throw new UnsupportedOperationException("Utility class");
	}

	// Consumer Topics (외부에서 수신)
	public static final String PROJECT_CREATED = "project-created";

	// Producer Topics (외부로 발행)
	public static final String REWARD_CANCELLATION = "reward-cancellation";
	public static final String FUNDING_COMPLETED = "funding-completed";
	public static final String FUNDING_CANCELLED = "funding-cancelled";
}