package com.technokratos.eateasy.product.exception;

import com.technokratos.eateasy.common.exception.ConflictServiceException;

public class ProductAlreadyExistsException extends ConflictServiceException {
  public ProductAlreadyExistsException(String message) {
    super(message);
  }
}
