package com.nowayback.project.domain.exception;

public class ProjectException extends RuntimeException{

    private final ProjectErrorCode errorCode;

    public ProjectException(ProjectErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ProjectErrorCode getErrorCode() {
        return errorCode;
    }
}
