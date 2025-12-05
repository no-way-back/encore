package com.nowayback.funding.domain.event;

public final class FundingProducerTopics {

	private FundingProducerTopics() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static final String FUNDING_FAILED = "funding-failed";
	public static final String FUNDING_REFUND = "funding-refund";
	public static final String FUNDING_COMPLETED = "funding-completed";
	public static final String PROJECT_FUNDING_SUCCESS = "project-funding-success";
	public static final String PROJECT_FUNDING_FAILED = "project-funding-failed";
	public static final String PROJECT_FUNDING_CREATED_FAILED = "project-funding-created-failed";
}