package com.nowayback.funding.infrastructure.config;

public final class KafkaConsumerTopics {

	private KafkaConsumerTopics() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static final String PROJECT_FUNDING_CREATION = "project-funding-creation";
}