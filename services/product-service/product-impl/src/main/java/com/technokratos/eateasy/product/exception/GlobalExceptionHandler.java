package com.technokratos.eateasy.product.exception;

import com.technokratos.eateasy.product.dto.exception.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
    String errorMessage =
        ex.getBindingResult().getAllErrors().stream()
            .map(
                error -> {
                  if (error instanceof FieldError fieldError) {
                    return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                  } else {
                    return error.getDefaultMessage();
                  }
                })
            .collect(Collectors.joining("; "));

    return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errorMessage);
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ErrorResponse> handleHandlerMethodValidation(
      HandlerMethodValidationException ex) {
    String errorMessage =
        ex.getAllErrors().stream()
            .map(
                error -> {
                  if (error instanceof FieldError fieldError) {
                    return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                  } else {
                    return error.getDefaultMessage();
                  }
                })
            .collect(Collectors.joining("; "));

    return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errorMessage);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
    String errorMessage =
        ex.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining("; "));
    return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errorMessage);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
    return buildErrorResponse(HttpStatus.CONFLICT, "Database constraint violated", ex.getMessage());
  }

  @ExceptionHandler(CategoryAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleCategoryAlreadyExists(
      CategoryAlreadyExistsException ex) {
    return buildErrorResponse(HttpStatus.CONFLICT, "Category already exists", ex.getMessage());
  }

  @ExceptionHandler(ProductAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleProductAlreadyExists(
      ProductAlreadyExistsException ex) {
    return buildErrorResponse(HttpStatus.CONFLICT, "Product already exists", ex.getMessage());
  }

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, "Product not found", ex.getMessage());
  }

  @ExceptionHandler(CategoryNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleProductNotFound(CategoryNotFoundException ex) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, "Category not found", ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralError(Exception ex) {
    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(
      HttpStatus status, String message, String details) {
    return ResponseEntity.status(status)
        .body(new ErrorResponse(status.value(), message, details, LocalDateTime.now()));
  }
}
