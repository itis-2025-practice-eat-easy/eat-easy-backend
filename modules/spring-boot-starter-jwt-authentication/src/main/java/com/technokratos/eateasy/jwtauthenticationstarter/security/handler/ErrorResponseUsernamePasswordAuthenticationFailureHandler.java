package com.technokratos.eateasy.jwtauthenticationstarter.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;

/**
 * Authentication failure handler for username/password authentication flow.
 * <p>
 * Maps common Spring Security exceptions to appropriate HTTP status codes and
 * client-friendly messages while preventing sensitive information leakage.
 * </p>
 */
@Slf4j
public class ErrorResponseUsernamePasswordAuthenticationFailureHandler
        extends AbstractErrorResponseAuthenticationFailureHandler {

    public ErrorResponseUsernamePasswordAuthenticationFailureHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    /**
     * Maps exception types to HTTP status codes:
     * <ul>
     *   <li>BadCredentialsException → 401 Unauthorized</li>
     *   <li>Account status exceptions → 403 Forbidden</li>
     *   <li>AuthenticationServiceException → 500 Internal Server Error</li>
     *   <li>Others → 400 Bad Request</li>
     * </ul>
     */
    protected HttpStatus determineHttpStatus(AuthenticationException ex) {
        if (ex instanceof BadCredentialsException) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (ex instanceof LockedException
            || ex instanceof DisabledException
            || ex instanceof AccountExpiredException
            || ex instanceof CredentialsExpiredException) {
            return HttpStatus.FORBIDDEN;
        }
        if (ex instanceof AuthenticationServiceException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.BAD_REQUEST;
    }

    /**
     * Generates safe client messages for exception types:
     * <ul>
     *   <li>Internal errors → Generic message</li>
     *   <li>Bad credentials → "Invalid username or password"</li>
     *   <li>Account status issues → Specific status description</li>
     * </ul>
     */
    protected String determineClientMessage(AuthenticationException ex) {
        if (ex instanceof AuthenticationServiceException) {
            return "Internal authentication error";
        }

        if (ex instanceof BadCredentialsException) {
            return "Invalid username or password";
        }
        if (ex instanceof LockedException) {
            return "Account is locked";
        }
        if (ex instanceof DisabledException) {
            return "Account is disabled";
        }
        if (ex instanceof AccountExpiredException) {
            return "Account expired";
        }
        if (ex instanceof CredentialsExpiredException) {
            return "Credentials expired";
        }
        return "Authentication failed";
    }

}
