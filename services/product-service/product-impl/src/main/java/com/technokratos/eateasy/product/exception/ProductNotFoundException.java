package com.technokratos.eateasy.product.exception;

import com.technokratos.eateasy.common.exception.NotFoundServiceException;

public class ProductNotFoundException extends NotFoundServiceException {
  public ProductNotFoundException(String message) {
    super(message);
  }
}
