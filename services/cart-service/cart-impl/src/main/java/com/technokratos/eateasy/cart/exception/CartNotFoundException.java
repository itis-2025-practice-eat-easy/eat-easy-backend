package com.technokratos.eateasy.cart.exception;

import com.technokratos.eateasy.common.exception.NotFoundServiceException;

import java.io.Serializable;
import java.util.UUID;

public class CartNotFoundException extends NotFoundServiceException {
    public CartNotFoundException(UUID cartId) {
        super("Cart with id %s not found".formatted(cartId));
    }
}
