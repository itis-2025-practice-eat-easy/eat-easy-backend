package com.technokratos.eateasy.product.exception;

import com.technokratos.eateasy.common.exception.ConflictServiceException;

public class CategoryAlreadyExistsException extends ConflictServiceException {
  public CategoryAlreadyExistsException(String message) {
    super(message);
  }
}
