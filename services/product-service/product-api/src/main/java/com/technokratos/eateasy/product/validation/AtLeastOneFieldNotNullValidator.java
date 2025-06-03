package com.technokratos.eateasy.product.validation;

import com.technokratos.eateasy.product.dto.product.ProductUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneFieldNotNullValidator
    implements ConstraintValidator<AtLeastOneFieldNotNull, ProductUpdateRequest> {

  String message;

  @Override
  public void initialize(AtLeastOneFieldNotNull constraintAnnotation) {
    message = constraintAnnotation.message();
  }

  @Override
  public boolean isValid(ProductUpdateRequest request, ConstraintValidatorContext context) {
    boolean valid =
        request.title() != null
            || request.description() != null
            || request.price() != null
            || request.categories() != null
            || request.quantity() != null;

    if (!valid) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    return valid;
  }
}
