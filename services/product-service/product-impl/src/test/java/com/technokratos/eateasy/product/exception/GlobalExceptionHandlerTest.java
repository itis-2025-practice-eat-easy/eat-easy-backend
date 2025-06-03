package com.technokratos.eateasy.product.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.technokratos.eateasy.product.dto.exception.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
  }

  @Test
  void handleMethodArgumentNotValid_shouldReturnBadRequest() {
    var bindingResult = new BeanPropertyBindingResult(new Object(), "test");
    bindingResult.addError(new FieldError("test", "field1", "must not be null"));

    var ex = new MethodArgumentNotValidException(null, bindingResult);

    ResponseEntity<ErrorResponse> response = handler.handleValidationErrors(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().message()).isEqualTo("Validation failed");
    assertThat(response.getBody().details()).contains("field1: must not be null");
  }

  @Test
  void handleConstraintViolation_shouldReturnBadRequest() {
    ConstraintViolation<?> mockViolation = mock(ConstraintViolation.class);
    when(mockViolation.getPropertyPath()).thenReturn(PathImpl.createPathFromString("field2"));
    when(mockViolation.getMessage()).thenReturn("must be positive");

    ConstraintViolationException ex = new ConstraintViolationException(Set.of(mockViolation));

    ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().message()).isEqualTo("Validation failed");
    assertThat(response.getBody().details()).contains("field2: must be positive");
  }

  @Test
  void handleDataIntegrity_shouldReturnConflict() {
    var ex = new DataIntegrityViolationException("duplicate key");

    ResponseEntity<ErrorResponse> response = handler.handleDataIntegrity(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody().message()).isEqualTo("Database constraint violated");
    assertThat(response.getBody().details()).contains("duplicate key");
  }

  @Test
  void handleCategoryAlreadyExists_shouldReturnConflict() {
    var ex = new CategoryAlreadyExistsException("Already exists");

    ResponseEntity<ErrorResponse> response = handler.handleCategoryAlreadyExists(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody().message()).isEqualTo("Category already exists");
    assertThat(response.getBody().details()).contains("Already exists");
  }

  @Test
  void handleProductAlreadyExists_shouldReturnConflict() {
    var ex = new ProductAlreadyExistsException("Product in system");

    ResponseEntity<ErrorResponse> response = handler.handleProductAlreadyExists(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody().message()).isEqualTo("Product already exists");
    assertThat(response.getBody().details()).contains("Product in system");
  }

  @Test
  void handleProductNotFound_shouldReturnNotFound() {
    var ex = new ProductNotFoundException("Not found");

    ResponseEntity<ErrorResponse> response = handler.handleProductNotFound(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody().message()).isEqualTo("Product not found");
    assertThat(response.getBody().details()).contains("Not found");
  }

  @Test
  void handleCategoryNotFound_shouldReturnNotFound() {
    var ex = new CategoryNotFoundException("Missing category");

    ResponseEntity<ErrorResponse> response = handler.handleProductNotFound(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody().message()).isEqualTo("Category not found");
    assertThat(response.getBody().details()).contains("Missing category");
  }

  @Test
  void handleGeneralError_shouldReturnInternalServerError() {
    var ex = new RuntimeException("unexpected error");

    ResponseEntity<ErrorResponse> response = handler.handleGeneralError(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody().message()).isEqualTo("Internal Server Error");
    assertThat(response.getBody().details()).contains("unexpected error");
  }
}
