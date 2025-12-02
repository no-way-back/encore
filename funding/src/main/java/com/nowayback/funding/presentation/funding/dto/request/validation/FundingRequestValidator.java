package com.nowayback.funding.presentation.funding.dto.request.validation;

import com.nowayback.funding.presentation.funding.dto.request.CreateFundingRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FundingRequestValidator implements ConstraintValidator<ValidFundingRequest, CreateFundingRequest> {

	private static final Long MIN_DONATION_AMOUNT = 1000L;

	@Override
	public boolean isValid(CreateFundingRequest request, ConstraintValidatorContext context) {
		if (request == null) {
			return false;
		}

		boolean hasRewards = request.rewardItems() != null && !request.rewardItems().isEmpty();
		boolean hasAmount = request.donationAmount() != null && request.donationAmount() > 0;

		if (!hasRewards && !hasAmount) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(
					"리워드 또는 후원 금액 중 하나는 필수입니다."
				)
				.addConstraintViolation();
			return false;
		}

		if (!hasRewards && request.donationAmount() < MIN_DONATION_AMOUNT) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(
					"순수 후원 금액은 최소 " + MIN_DONATION_AMOUNT + "원 이상이어야 합니다."
				)
				.addPropertyNode("amount")
				.addConstraintViolation();
			return false;
		}

		return true;
	}
}
