package com.technokratos.eateasy.common.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

/**
 * Standardized error response structure for API error reporting.
 * <p>
 * Contains timestamp, HTTP status code, request path, error description,
 * and optional details payload. Built using the fluent builder pattern.
 */
@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    /**
     * UTC timestamp when error occurred
     */
    protected final ZonedDateTime timestamp;

    /**
     * HTTP status code (e.g., 400)
     */
    protected final int httpStatus;

    /**
     * Request path that triggered the error
     */
    protected final String path;

    /**
     * Human-readable error description
     */
    protected final String error;

    /**
     * Optional structured error details (may be {@code null})
     */
    protected final Object details;


    public static AbstractErrorResponseBuilder<?, ?> builder() {
        return new ErrorResponseBuilderImpl();
    }

    /**
     * Builder implementation with validation logic.
     * <p>
     * Requires {@code httpStatus}, {@code path}, and {@code error} to be set
     * before building. Logs validation failures and throws {@code IllegalStateException}
     * for missing required fields.
     */
    public static class ErrorResponseBuilderImpl extends AbstractErrorResponseBuilder<ErrorResponse, ErrorResponseBuilderImpl> {

        @Override
        protected ErrorResponseBuilderImpl self() {
            return this;
        }

        @Override
        public ErrorResponse build() {
            if (!isHttpStatusSet || error == null || path == null) {
                log.error("Required fields not set: httpStatus={}, error={}, path={}", isHttpStatusSet, error, path);
                throw new IllegalStateException("Required fields not set");
            }
            return new ErrorResponse(timestamp, httpStatus, path, error, details);
        }
    }


    /**
     * Abstract base builder with common configuration methods.
     * @param <E> Concrete ErrorResponse type
     * @param <B> Concrete builder type (for fluent chaining)
     */
    public static abstract class AbstractErrorResponseBuilder<E extends ErrorResponse, B extends AbstractErrorResponseBuilder<E, B>> {

        protected final ZonedDateTime timestamp;
        protected int httpStatus;
        protected boolean isHttpStatusSet = false;
        protected String error;
        protected String path;
        protected Object details;

        public AbstractErrorResponseBuilder() {
            this.timestamp = ZonedDateTime.now();
        }

        public B httpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus.value();
            this.isHttpStatusSet = true;
            return self();
        }

        public B error(String error) {
            this.error = error;
            return self();
        }

        public B path(String path) {
            this.path = path;
            return self();
        }

        public B details(Object details) {
            this.details = details;
            return self();
        }

        protected abstract B self();

        public abstract E build();
    }
}