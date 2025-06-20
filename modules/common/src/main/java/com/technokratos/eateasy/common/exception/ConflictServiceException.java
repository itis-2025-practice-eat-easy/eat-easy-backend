package com.technokratos.eateasy.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents 409 Conflict error.
 */
public class ConflictServiceException extends ClientErrorServiceException{

    public ConflictServiceException(String message, Object details, Throwable cause) {
        super(message, HttpStatus.CONFLICT, details, cause);
    }

    public ConflictServiceException(String message, Object details) {
        super(message, HttpStatus.CONFLICT, details);
    }

    public ConflictServiceException(String message, Throwable cause) {
        super(message, HttpStatus.CONFLICT, cause);
    }

    public ConflictServiceException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
