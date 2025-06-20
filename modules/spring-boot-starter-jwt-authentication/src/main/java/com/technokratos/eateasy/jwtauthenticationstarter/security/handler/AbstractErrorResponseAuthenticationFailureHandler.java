package com.technokratos.eateasy.jwtauthenticationstarter.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.eateasy.common.dto.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Base class for authentication failure handlers producing standardized error responses.
 * <p>
 * Converts Spring Security authentication exceptions to JSON error responses using
 * {@link ErrorResponse} format. Requires concrete implementations to define:
 * <ul>
 *   <li>HTTP status mapping logic</li>
 *   <li>Client-friendly error messages</li>
 * </ul>
 * </p>
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractErrorResponseAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        HttpStatus httpStatus = determineHttpStatus(exception);
        String clientMessage = determineClientMessage(exception);

        logError(httpStatus, exception);

        sendErrorResponse(request, response, httpStatus, clientMessage);
    }

    /**
     * Determines appropriate HTTP status for exception type
     */
    protected abstract HttpStatus determineHttpStatus(AuthenticationException ex);

    /**
     * Creates client-safe error message for exception
     */
    protected abstract String determineClientMessage(AuthenticationException ex);

    protected void logError(HttpStatus httpStatus, AuthenticationException e) {
        if (httpStatus.is5xxServerError()) {
            log.error("Server error during authentication: {}", e.getMessage(), e);
        } else {
            log.info("Authentication failed: {}", e.getMessage());
        }
    }

    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response, HttpStatus status,
                                   String message) throws IOException, ServletException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ErrorResponse errorResponse = createErrorResponse(request.getRequestURI(), status, message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }

    protected ErrorResponse createErrorResponse(String path, HttpStatus status, String error) {
        return ErrorResponse.builder()
                .httpStatus(status)
                .error(error)
                .path(path)
                .build();
    }
}
