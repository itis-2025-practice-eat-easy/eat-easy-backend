package com.technokratos.eateasy.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents 404 Not Found error.
 */
public class NotFoundServiceException extends ClientErrorServiceException {

    public NotFoundServiceException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public NotFoundServiceException(String message, Throwable cause) {
        super(message, HttpStatus.NOT_FOUND, cause);
    }

    public NotFoundServiceException(String message, Object details) {
        super(message, HttpStatus.NOT_FOUND, details);
    }

    public NotFoundServiceException(String message, Object details, Throwable cause) {
        super(message, HttpStatus.NOT_FOUND, details, cause);
    }
}
