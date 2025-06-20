package com.technokratos.eateasy.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents 500 Internal Server Error.
 */
public class InternalServiceException extends ServerErrorServiceException {

    public InternalServiceException(String message, Object details, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, details, cause);
    }

    public InternalServiceException(String message, Object details) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, details);
    }

    public InternalServiceException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }

    public InternalServiceException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
