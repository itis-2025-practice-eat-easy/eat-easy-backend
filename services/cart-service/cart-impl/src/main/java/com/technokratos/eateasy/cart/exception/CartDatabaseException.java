package com.technokratos.eateasy.cart.exception;

import com.technokratos.eateasy.common.exception.InternalServiceException;

import java.util.UUID;

public class CartDatabaseException extends InternalServiceException {
    public CartDatabaseException(UUID cartId) {
        super("Database error during cart operation %s".formatted(cartId));
    }
}
