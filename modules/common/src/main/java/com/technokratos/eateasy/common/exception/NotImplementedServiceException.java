package com.technokratos.eateasy.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents 501 Not Implemented error.
 */
public class NotImplementedServiceException extends ServerErrorServiceException {
    
    public NotImplementedServiceException(String message, Object details, Throwable cause) {
        super(message, HttpStatus.NOT_IMPLEMENTED, details, cause);
    }

    public NotImplementedServiceException(String message, Object details) {
        super(message, HttpStatus.NOT_IMPLEMENTED, details);
    }

    public NotImplementedServiceException(String message, Throwable cause) {
        super(message, HttpStatus.NOT_IMPLEMENTED, cause);
    }

    public NotImplementedServiceException(String message) {
        super(message, HttpStatus.NOT_IMPLEMENTED);
    }
}
