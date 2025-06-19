package com.technokratos.eateasy.cart.exception;

import com.technokratos.eateasy.common.exception.ClientErrorServiceException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class CartIsBlockedException extends ClientErrorServiceException {
    public CartIsBlockedException(UUID cartId) {
        super("Cart with id: %s is blocked".formatted(cartId), HttpStatus.FORBIDDEN);
    }
}
