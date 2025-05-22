package com.technokratos.eateasy.product.controller;


import com.technokratos.eateasy.product.api.ProductApi;
import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.dto.product.ProductUpdateRequest;
import com.technokratos.eateasy.product.service.ProductCategoryFacade;
import com.technokratos.eateasy.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductController implements ProductApi {

    private final ProductService productService;
    private final ProductCategoryFacade productCategoryFacade;

    @Override
    public ProductResponse getById(UUID id) {
        log.info("Received request to get product by id: {}", id);
        return productCategoryFacade.getById(id);
    }

    @Override
    public ProductResponse create(@Valid @RequestBody ProductRequest product) {
        log.info("Received request to create product: {}", product);
        return productCategoryFacade.create(product);
    }

    @Override
    public void updateQuantity(UUID id, @Valid @RequestBody Integer quantity) {
        log.info("Received request to update product quantity: {},  with id: {}", quantity, id);
        productService.updateQuantity(id, quantity);
    }

    @Override
    public void update(UUID id, @Valid @RequestBody ProductUpdateRequest product) {
        log.info("Received request to update product with id: {}, data: {}", id, product);
        productCategoryFacade.update(id, product);
    }

    @Override
    public void delete(UUID id) {
        log.info("Received request to delete product with id: {}", id);
        productService.delete(id);
    }
}
