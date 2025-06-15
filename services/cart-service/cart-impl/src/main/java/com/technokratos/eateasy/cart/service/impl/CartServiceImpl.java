package com.technokratos.eateasy.cart.service.impl;

import com.technokratos.dto.CartResponse;
import com.technokratos.dto.ProductRequest;
import com.technokratos.eateasy.cart.entity.Cart;
import com.technokratos.eateasy.cart.exception.CartNotFoundException;
import com.technokratos.eateasy.cart.mapper.CartMapper;
import com.technokratos.eateasy.cart.repository.CartRepository;
import com.technokratos.eateasy.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository repository;
    private final CartMapper mapper;

    public CartResponse getById(UUID id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new CartNotFoundException(id)));
    }

    public void addToCart(ProductRequest request, UUID cartId) {
        repository.addToCart(request.id(), request.quantity(), cartId);
        return;
    }

    public List<CartResponse> getAll(UUID id) {
        return repository.findAll(id).stream()
                .map(mapper::toResponse)
                .toList();
    }
}
