package com.technokratos.eateasy.product.exception;

import com.technokratos.eateasy.common.exception.NotFoundServiceException;

public class CategoryNotFoundException extends NotFoundServiceException {
  public CategoryNotFoundException(String message) {
    super(message);
  }
}
