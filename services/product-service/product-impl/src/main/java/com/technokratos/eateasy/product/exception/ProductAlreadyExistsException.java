package com.technokratos.eateasy.product.exception;

import com.technokratos.eateasy.common.exception.ClientErrorServiceException;
import org.springframework.http.HttpStatus;

public class ProductAlreadyExistsException extends ClientErrorServiceException {
  public ProductAlreadyExistsException(String message) {
    super(message, HttpStatus.CONFLICT);
  }
}
