package com.technokratos.eateasy.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base class for all client error exceptions (HTTP 4xx status codes).
 * Validates status codes belong to 4xx range.
 *
 * @see BadRequestServiceException
 * @see NotFoundServiceException
 * @see ConflictServiceException
 * @see UnprocessableEntityServiceException
 */
public abstract class ClientErrorServiceException extends ServiceException {

    public ClientErrorServiceException(String message, HttpStatus httpStatus, Object details, Throwable cause) {
        super(message, checkIfIsClientError(httpStatus), details, cause);
    }

    public ClientErrorServiceException(String message, HttpStatus httpStatus, Object details) {
        this(message, httpStatus, details, null);
    }

    public ClientErrorServiceException(String message, HttpStatus httpStatus, Throwable cause) {
        this(message, httpStatus, null, cause);
    }

    public ClientErrorServiceException(String message, HttpStatus httpStatus) {
        this(message, httpStatus, null, null);
    }

    private static HttpStatus checkIfIsClientError(HttpStatus httpStatus) {
        if (httpStatus.is4xxClientError()) {
            return httpStatus;
        }
        throw new IllegalArgumentException("HttpStatus must be 4xx");
    }
}
