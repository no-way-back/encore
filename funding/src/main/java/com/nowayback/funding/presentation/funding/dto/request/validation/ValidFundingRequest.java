package com.nowayback.funding.presentation.funding.dto.request.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FundingRequestValidator.class)
public @interface ValidFundingRequest {

	String message() default "리워드 또는 후원 금액 중 하나는 필수입니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
