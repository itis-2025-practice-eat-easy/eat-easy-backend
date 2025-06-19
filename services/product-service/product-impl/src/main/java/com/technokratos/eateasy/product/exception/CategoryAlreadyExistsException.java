package com.technokratos.eateasy.product.exception;

import com.technokratos.eateasy.common.exception.ClientErrorServiceException;
import org.springframework.http.HttpStatus;

public class CategoryAlreadyExistsException extends ClientErrorServiceException {
  public CategoryAlreadyExistsException(String message) {
    super(message, HttpStatus.FORBIDDEN);
  }
}
