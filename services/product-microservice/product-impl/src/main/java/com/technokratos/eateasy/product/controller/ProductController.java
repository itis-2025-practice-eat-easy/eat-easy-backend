package com.technokratos.eateasy.product.controller;


import com.technokratos.eateasy.product.api.ProductApi;
import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.service.ProductCategoryFacade;
import com.technokratos.eateasy.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductController implements ProductApi {

    private final ProductService productService;
    private final ProductCategoryFacade productCategoryFacade;

    @Override
    public ResponseEntity<ProductResponse> getById(UUID id) {
        log.info("Received request to get product by id: {}", id);
        return ResponseEntity.ok(productCategoryFacade.getById(id));
    }

    @Override
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest product) {
        log.info("Received request to create product: {}", product);
        ProductResponse savedProduct = productCategoryFacade.create(product);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedProduct.id())
                .toUri();
        return ResponseEntity.created(location).body(savedProduct);
    }

    @Override
    public ResponseEntity<Void> updateQuantity(UUID id, @Valid @RequestBody Integer quantity) {
        log.info("Received request to update product quantity: {},  with id: {}", quantity, id);
        productService.updateQuantity(id, quantity);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> update(UUID id, @RequestBody ProductRequest product) {
        log.info("Received request to update product with id: {}, data: {}", id, product);
        productCategoryFacade.update(id, product);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> delete(UUID id) {
        log.info("Received request to delete product with id: {}", id);
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
