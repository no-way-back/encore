package com.nowayback.user.presentation.exception;

import com.nowayback.user.domain.exception.UserException;
import com.nowayback.user.presentation.exception.response.ErrorResponse;
import com.nowayback.user.presentation.exception.response.FieldError;
import com.nowayback.user.presentation.exception.response.FieldErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException exception) {
        ErrorResponse response = new ErrorResponse(
                exception.getErrorCode().getCode(),
                exception.getErrorCode().getMessage(),
                exception.getErrorCode().getHttpStatus().value()
        );

        return ResponseEntity
                .status(exception.getErrorCode().getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FieldErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        List<FieldError> errorFields = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new FieldError(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()))
                .toList();

        return ResponseEntity
                .badRequest()
                .body(new FieldErrorResponse(
                        "INVALID_REQUEST",
                        "유효하지 않은 요청입니다.",
                        HttpStatus.BAD_REQUEST.value(),
                        errorFields));
    }
}
