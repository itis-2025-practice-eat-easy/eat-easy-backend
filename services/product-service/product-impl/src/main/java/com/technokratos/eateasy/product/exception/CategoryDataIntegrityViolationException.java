package com.technokratos.eateasy.product.exception;

import com.technokratos.eateasy.common.exception.ConflictServiceException;

public class CategoryDataIntegrityViolationException extends ConflictServiceException {
    public CategoryDataIntegrityViolationException(String message) {
        super(message);
    }
}
