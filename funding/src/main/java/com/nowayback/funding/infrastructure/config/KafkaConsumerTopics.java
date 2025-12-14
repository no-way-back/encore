package com.nowayback.funding.infrastructure.config;

public final class KafkaConsumerTopics {

	private KafkaConsumerTopics() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static final String PROJECT_FUNDING_CREATION = "project-funding-creation";
	public static final String PAYMENT_CONFIRM_SUCCEEDED = "payment-confirm-succeeded";
	public static final String PAYMENT_CONFIRM_FAILED = "payment-confirm-failed";
}