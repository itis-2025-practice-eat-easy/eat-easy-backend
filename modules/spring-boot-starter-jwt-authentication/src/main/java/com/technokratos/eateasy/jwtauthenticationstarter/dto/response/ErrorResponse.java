package com.technokratos.eateasy.jwtauthenticationstarter.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

/**
 * Standardized error response format for Auth API error handling.
 * <p>
 * Contains timestamp, HTTP status code, request path, error description,
 * and optional details. Uses builder pattern with validation for required fields.
 *
 */
@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    protected final ZonedDateTime timestamp;
    protected final int httpStatus;
    protected final String path;
    protected final String error;
    protected final Object details;


    public static AbstractErrorResponseBuilder<?, ?> builder() {
        return new ErrorResponseBuilderImpl();
    }

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
