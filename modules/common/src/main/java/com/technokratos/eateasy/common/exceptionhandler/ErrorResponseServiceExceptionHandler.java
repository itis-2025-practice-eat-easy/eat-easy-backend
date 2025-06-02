package com.technokratos.eateasy.common.exceptionhandler;

import com.technokratos.eateasy.common.dto.response.ErrorResponse;
import com.technokratos.eateasy.common.exception.ClientErrorServiceException;
import com.technokratos.eateasy.common.exception.ServerErrorServiceException;
import com.technokratos.eateasy.common.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler that converts {@link ServiceException} hierarchy
 * into standardized {@link ErrorResponse} payloads.
 * <p>
 * Automatically maps exception properties to response fields:
 * <ul>
 *   <li>HTTP status code from {@code ServiceException.getStatus()}</li>
 *   <li>Error message from exception's message</li>
 *   <li>Request path from servlet request</li>
 *   <li>Additional details from exception's details field</li>
 * </ul>
 *
 * @see ControllerAdvice
 * @see ServiceException
 */
@Slf4j
@ControllerAdvice
public class ErrorResponseServiceExceptionHandler {


    @ExceptionHandler(ClientErrorServiceException.class)
    public ResponseEntity<ErrorResponse> handleClientErrorServiceException(ClientErrorServiceException e, HttpServletRequest request) {
        log.info("Client error occurred: path={}, error={}", request.getRequestURI(), e.getMessage());
        return ResponseEntity
                .status(e.getStatus())
                .body(createErrorResponse(e, request));
    }

    @ExceptionHandler(ServerErrorServiceException.class)
    public ResponseEntity<ErrorResponse> handleServerErrorServiceException(ServerErrorServiceException e, HttpServletRequest request) {
        log.error("Server error occurred: path={}, error={}", request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity
                .status(e.getStatus())
                .body(createErrorResponse(e, request));
    }

    private ErrorResponse createErrorResponse(ServiceException e, HttpServletRequest request) {
        return ErrorResponse.builder()
                .httpStatus(e.getStatus())
                .path(request.getRequestURI())
                .error(e.getMessage())
                .details(e.getDetails())
                .build();
    }
}
