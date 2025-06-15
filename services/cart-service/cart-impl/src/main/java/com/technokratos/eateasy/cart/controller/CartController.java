package com.technokratos.eateasy.cart.controller;

import com.technokratos.api.CartApi;
import com.technokratos.dto.CartRequest;
import com.technokratos.dto.CartResponse;
import com.technokratos.dto.ProductRequest;
import com.technokratos.eateasy.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CartController implements CartApi {

    private final CartService cartService;

    @Override
    public CartResponse create(CartRequest cart) {
        log.info("Received request to create cart: {}", cart);
        return cartService.create(cart);
    }

    @Override
    public List<CartResponse> getAll(UUID id) {
        log.info("Received request to get all user's carts");
        return cartService.getAll(id);
    }

    @Override
    public void addToCart(ProductRequest product, UUID userId) {
        log.info("Received request to add product to cart: {}", product.id());
        cartService.addToCart(product, userId);
    }

    @Override
    public CartResponse getById(UUID id) {
        log.info("Received request to get cart by id: {}", id);
        return cartService.getById(id);
    }
}
