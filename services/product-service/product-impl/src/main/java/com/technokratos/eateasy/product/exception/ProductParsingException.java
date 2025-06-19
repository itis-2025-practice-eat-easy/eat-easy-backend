package com.technokratos.eateasy.product.exception;

import com.technokratos.eateasy.common.exception.ServerErrorServiceException;
import org.springframework.http.HttpStatus;

public class ProductParsingException extends ServerErrorServiceException {
    public ProductParsingException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
