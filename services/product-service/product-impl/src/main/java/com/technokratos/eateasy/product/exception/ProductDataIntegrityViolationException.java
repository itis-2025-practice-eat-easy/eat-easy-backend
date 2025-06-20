package com.technokratos.eateasy.product.exception;

import com.technokratos.eateasy.common.exception.ConflictServiceException;

public class ProductDataIntegrityViolationException extends ConflictServiceException {
    public ProductDataIntegrityViolationException(String message) {
        super(message);
    }
}
