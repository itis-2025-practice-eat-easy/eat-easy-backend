package com.technokratos.eateasy.cart.exception;

import com.technokratos.eateasy.common.exception.ConflictServiceException;

import java.util.UUID;

public class CartIntegrityViolationException extends ConflictServiceException {
    public CartIntegrityViolationException(UUID cartId) {
        super("Database constraint violated while saving cart with id: %s".formatted(cartId));
    }
}
