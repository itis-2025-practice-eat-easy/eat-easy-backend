package com.technokratos.eateasy.jwtauthenticationstarter.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;

/**
 * Token-specific authentication failure handler producing standardized error responses.
 * <p>
 * Maps common token authentication exceptions to HTTP status codes and client messages
 * with context-specific token type naming (access/refresh).
 * </p>
 */
public class ErrorResponseTokenAuthenticationFailureHandler extends AbstractErrorResponseAuthenticationFailureHandler {
    /**
     * Name of the token type (e.g., "access" or "refresh") used in error messages.
     */
    private final String tokenName;

    public ErrorResponseTokenAuthenticationFailureHandler(ObjectMapper objectMapper, String tokenName) {
        super(objectMapper);
        this.tokenName = tokenName;
    }
    
    @Override
    protected HttpStatus determineHttpStatus(AuthenticationException ex) {
        if (ex instanceof BadCredentialsException || ex instanceof CredentialsExpiredException) {
            return HttpStatus.UNAUTHORIZED;
        }

        if (ex instanceof AuthenticationServiceException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.BAD_REQUEST;
    }


    @Override
    protected String determineClientMessage(AuthenticationException ex) {
        if (ex instanceof BadCredentialsException) {
            return "Invalid %s token".formatted(tokenName);
        }

        if (ex instanceof CredentialsExpiredException) {
            return "%s token expired".formatted(StringUtils.capitalize(tokenName));
        }

        if (ex instanceof AuthenticationServiceException) {
            return "Internal authentication error";
        }

        return "Authentication failed";
    }
}
