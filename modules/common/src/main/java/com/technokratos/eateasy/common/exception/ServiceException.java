package com.technokratos.eateasy.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

/**
 * Base exception class for business layer operations. Intended to be extended
 * by specific exception types (e.g., {@code NotFoundException}) rather than
 * thrown directly. Contains HTTP status information and optional details
 * about the error.
 *
 * @see HttpStatus
 */
public abstract class ServiceException extends RuntimeException {

    /**
     * HTTP status code associated with the exception.
     */
    @Getter
    public final HttpStatus status;

    private final Object details;


    /**
     * Constructs a new service exception with message, HTTP status, details, and cause.
     *
     * @param message    the detail message (required)
     * @param httpStatus the HTTP status code (required)
     * @param details    additional error details (may be {@code null})
     * @param cause      the root cause (may be {@code null})
     */
    public ServiceException(String message, HttpStatus httpStatus, Object details, Throwable cause) {
        super(message, cause);
        this.details = details;
        this.status = httpStatus;
    }

    /**
     * Constructs a new service exception with message, HTTP status, and details.
     * The cause is initialized to {@code null}.
     *
     * @param message    the detail message
     * @param httpStatus the HTTP status code
     * @param details    additional error details
     */
    public ServiceException(String message, HttpStatus httpStatus, Object details) {
        this(message, httpStatus, details, null);
    }

    /**
     * Constructs a new service exception with message, HTTP status, and cause.
     * Details are initialized to {@code null}.
     *
     * @param message    the detail message
     * @param httpStatus the HTTP status code
     * @param cause      the root cause
     */
    public ServiceException(String message, HttpStatus httpStatus, Throwable cause) {
        this(message, httpStatus, null, cause);
    }

    /**
     * Constructs a new service exception with message and HTTP status.
     * Details and cause are initialized to {@code null}.
     *
     * @param message    the detail message
     * @param httpStatus the HTTP status code
     */
    public ServiceException(String message, HttpStatus httpStatus) {
        this(message, httpStatus, null, null);
    }


    /**
     * Returns additional error details, if any.
     *
     * @return details object, or {@code null} if none
     */
    @Nullable
    public Object getDetails() {
        return details;
    }
}