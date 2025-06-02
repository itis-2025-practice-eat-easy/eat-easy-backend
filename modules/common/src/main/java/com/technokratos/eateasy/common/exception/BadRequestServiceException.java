package com.technokratos.eateasy.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents a 400 Bad Request error.
 */
public class BadRequestServiceException extends ClientErrorServiceException {

    public BadRequestServiceException(String message, Object details, Throwable cause) {
        super(message, HttpStatus.BAD_REQUEST, details, cause);
    }

    public BadRequestServiceException(String message, Object details) {
        super(message, HttpStatus.BAD_REQUEST, details);
    }

    public BadRequestServiceException(String message, Throwable cause) {
        super(message, HttpStatus.BAD_REQUEST, cause);
    }

    public BadRequestServiceException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
