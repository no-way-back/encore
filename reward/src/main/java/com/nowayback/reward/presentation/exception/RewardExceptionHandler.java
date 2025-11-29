package com.nowayback.reward.presentation.exception;

import java.util.List;

import com.nowayback.reward.domain.exception.RewardException;
import com.nowayback.reward.presentation.exception.response.ErrorResponse;
import com.nowayback.reward.presentation.exception.response.FieldError;
import com.nowayback.reward.presentation.exception.response.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import static com.nowayback.reward.domain.exception.RewardErrorCode.*;

@RestControllerAdvice
@Slf4j
public class RewardExceptionHandler {

    @ExceptionHandler(RewardException.class)
    public ResponseEntity<ErrorResponse> handleRewardException(RewardException e) {
        log.warn("비즈니스 규칙 위반: {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                e.getErrorCode().getStatus().value()
        );

        return ResponseEntity.status(e.getErrorCode().getStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException e) {

        log.warn("입력 검증 실패: {}", e.getMessage());

        List<FieldError> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
                .toList();

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                VALIDATION_FAILED.getCode(),
                VALIDATION_FAILED.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                errors
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(
            ConstraintViolationException e) {

        log.warn("제약 조건 위반: {}", e.getMessage());

        List<FieldError> errors = e.getConstraintViolations()
                .stream()
                .map(violation -> new FieldError(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .toList();

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                VALIDATION_FAILED.getCode(),
                VALIDATION_FAILED.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                errors
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("서버 내부 오류 발생", e);

        ErrorResponse errorResponse = new ErrorResponse(
                INTERNAL_SERVER_ERROR.getCode(),
                INTERNAL_SERVER_ERROR.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}