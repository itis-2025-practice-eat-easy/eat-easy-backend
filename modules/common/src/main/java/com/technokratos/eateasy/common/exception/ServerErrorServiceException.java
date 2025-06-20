package com.technokratos.eateasy.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base class for server error exceptions (HTTP 5xx status codes).
 * Validates status codes belong to 5xx range.
 *
 * @see InternalServiceException
 * @see NotImplementedServiceException
 * @see ServiceUnavailableServiceException
 */
public abstract class ServerErrorServiceException extends ServiceException {

    public ServerErrorServiceException(String message, HttpStatus httpStatus, Object details, Throwable cause) {
        super(message, checkIfIsServerError(httpStatus), details, cause);
    }

    public ServerErrorServiceException(String message, HttpStatus httpStatus, Object details) {
        this(message, httpStatus, details, null);
    }

    public ServerErrorServiceException(String message, HttpStatus httpStatus, Throwable cause) {
        this(message, httpStatus, null, cause);
    }

    public ServerErrorServiceException(String message, HttpStatus httpStatus) {
        this(message, httpStatus, null, null);
    }

    private static HttpStatus checkIfIsServerError(HttpStatus httpStatus) {
        if (httpStatus.is5xxServerError()) {
            return httpStatus;
        }
        throw new IllegalArgumentException("HttpStatus must be 5xx");
    }
}
