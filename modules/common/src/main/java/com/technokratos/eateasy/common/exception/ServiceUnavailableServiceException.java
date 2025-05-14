package com.technokratos.eateasy.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Represents 503 Service Unavailable error.
 */
public class ServiceUnavailableServiceException extends ServerErrorServiceException {

    public ServiceUnavailableServiceException(String message, Object details, Throwable cause) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE, details, cause);
    }

    public ServiceUnavailableServiceException(String message, Object details) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE, details);
    }

    public ServiceUnavailableServiceException(String message, Throwable cause) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE, cause);
    }

    public ServiceUnavailableServiceException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
