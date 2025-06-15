package com.technokratos.eateasy.cart.repository;

import com.technokratos.eateasy.cart.entity.Cart;

import java.util.List;
import java.util.UUID;

public interface CartRepository {
    public Cart save(Cart cart);
    public List<Cart> findAll(UUID id);
    public void addToCart(UUID productId, Integer quantity, UUID userId);
    public Cart findById(UUID id);
}
