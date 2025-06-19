package com.technokratos.eateasy.cart.repository;

import com.technokratos.eateasy.cart.entity.Cart;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository {
    public List<Cart> findAll(UUID id);
    public void addToCart(UUID productId, Integer quantity, UUID cartId);
    public Optional<Cart> findById(UUID id);
}
