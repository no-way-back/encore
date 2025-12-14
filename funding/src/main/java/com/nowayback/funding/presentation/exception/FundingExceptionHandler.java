package com.nowayback.funding.presentation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nowayback.funding.domain.exception.FundingException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class FundingExceptionHandler {

	@ExceptionHandler(FundingException.class)
	public ResponseEntity<ErrorResponse> handleFundingException(FundingException e) {

		log.warn("비즈니스 규칙 위반: {}", e.getMessage());

		ErrorResponse errorResponse = new ErrorResponse(
			e.getErrorCode().getCode(),
			e.getErrorCode().getMessage(),
			e.getErrorCode().getStatus().value()
		);

		return ResponseEntity.status(e.getErrorCode().getStatus()).body(errorResponse);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse>handleValidationException(MethodArgumentNotValidException e) {

		log.warn("입력 검증 실패: {}", e.getMessage());

		String message = e.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.findFirst()
			.orElse("입력 검증 실패");

		ErrorResponse errorResponse = new ErrorResponse(
			"FD000",
			message,
			HttpStatus.BAD_REQUEST.value()
		);

		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {
		log.error("서버 내부 오류 발생", e);

		ErrorResponse errorResponse = new ErrorResponse(
			"FD999",
			"일시적인 오류가 발생했습니다.",
			HttpStatus.INTERNAL_SERVER_ERROR.value()
		);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

	public record ErrorResponse(
		String code,
		String message,
		Integer status
	) {}
}