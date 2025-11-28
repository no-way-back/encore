package com.nowayback.project.presentation.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nowayback.project.domain.exception.ProjectException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectExceptionHandler {

    @ExceptionHandler(ProjectException.class)
    public ResponseEntity<ErrorResponse> handleException(ProjectException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
            exception.getErrorCode().getCode(),
            exception.getErrorCode().getMessage(),
            exception.getErrorCode().getStatus().value(),
            null
        );
        return ResponseEntity.status(exception.getErrorCode().getStatus())
            .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(
        MethodArgumentNotValidException exception
    ) {
        List<ErrorResponse.ErrorField> errorFields = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError -> new ErrorResponse.ErrorField(
                fieldError.getField(),
                fieldError.getDefaultMessage()))
            .toList();

        ErrorResponse errorResponse = new ErrorResponse(
            "API001",
            "잘못된 요청입니다.",
            HttpStatus.BAD_REQUEST.value(),
            errorFields
        );

        return ResponseEntity
            .badRequest()
            .body(errorResponse);
    }

    public record ErrorResponse(
        String code,
        String message,
        Integer status,
        @JsonInclude(JsonInclude.Include.NON_NULL) List<ErrorResponse.ErrorField> errors
    ) {

        public record ErrorField(Object value, String message) {

        }
    }
}
