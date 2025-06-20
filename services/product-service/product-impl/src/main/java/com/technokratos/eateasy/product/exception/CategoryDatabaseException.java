package com.technokratos.eateasy.product.exception;

import com.technokratos.eateasy.common.exception.ServerErrorServiceException;
import org.springframework.http.HttpStatus;

public class CategoryDatabaseException extends ServerErrorServiceException {
    public CategoryDatabaseException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
