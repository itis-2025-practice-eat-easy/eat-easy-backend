package com.technokratos.eateasy.product.controller;


import com.technokratos.eateasy.product.ProductApi;
import com.technokratos.eateasy.product.entity.Product;
import com.technokratos.eateasy.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductApi {

    private final ProductService productService;

    @Override
    public ResponseEntity<Product> getById(UUID productId) {
        return productService.getById(productId);
    }

    @Override
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
        return productService.create(product);
    }

    @Override
    public ResponseEntity<Void> updateQuantity(UUID productId, @Valid @RequestBody Integer quantity) {
        return productService.updateQuantity(productId, quantity);
    }

    @Override
    public ResponseEntity<Void> update(UUID productId, @RequestBody Product product) {
        return productService.update(productId, product);
    }

    @Override
    public ResponseEntity<Void> delete(UUID productId) {
        return productService.delete(productId);
    }
}
