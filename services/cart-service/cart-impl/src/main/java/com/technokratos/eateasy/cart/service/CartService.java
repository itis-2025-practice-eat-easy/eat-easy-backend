package com.technokratos.eateasy.cart.service;

import com.technokratos.dto.CartRequest;
import com.technokratos.dto.CartResponse;
import com.technokratos.dto.ProductRequest;

import java.util.List;
import java.util.UUID;

public interface CartService {
    public CartResponse create(CartRequest cart);
    public List<CartResponse> getAll(UUID id);
    public void addToCart(ProductRequest request, UUID userId);
    public CartResponse getById(UUID id);
}
