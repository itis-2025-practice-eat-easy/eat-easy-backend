package com.technokratos.eateasy.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents 422 Unprocessable Entity error.
 */
public class UnprocessableEntityServiceException extends ClientErrorServiceException {

    public UnprocessableEntityServiceException(String message, Object details, Throwable cause) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, details, cause);
    }

    public UnprocessableEntityServiceException(String message, Object details) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, details);
    }

    public UnprocessableEntityServiceException(String message, Throwable cause) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, cause);
    }

    public UnprocessableEntityServiceException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
