package com.technokratos.eateasy.userimpl.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException e) {
        String violations = e.getConstraintViolations().stream()
                .map(v -> String.format("Invalid value %s", v.getInvalidValue()))
                .collect(Collectors.joining());
        return ErrorResponse.builder(e, HttpStatus.BAD_REQUEST, request.getRequestURI())
                .detail("Validation failure: " + violations)
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGeneralException(HttpServletRequest request, Exception e) {
        return ErrorResponse.builder(e, HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI())
                .detail(String.format("Server error: %s", e.getMessage()))
                .build();
    }
}
