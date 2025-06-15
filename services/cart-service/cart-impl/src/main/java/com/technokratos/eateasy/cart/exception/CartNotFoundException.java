package com.technokratos.eateasy.cart.exception;

import java.io.Serializable;

public class CartNotFoundException extends RuntimeException{
    public CartNotFoundException(String s) {
        super(s);
    }
}
