package com.technokratos.eateasy.product.service;


import com.technokratos.eateasy.product.entity.Product;
import com.technokratos.eateasy.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ResponseEntity<Product> getById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElse(null);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    public ResponseEntity<Product> create(Product product) {
        try {
            Product savedProduct = productRepository.save(product);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedProduct.getId())
                    .toUri();

            return ResponseEntity
                    .created(location)
                    .body(savedProduct);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        }
    }

    public ResponseEntity<Void> updateQuantity(UUID productId, Integer quantity) {
        try {
            int updatedRows = productRepository.updateQuantityIfNotNegative(productId, quantity);

            if (updatedRows == 1) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        }
    }

    public ResponseEntity<Void> update(UUID id, Product product) {
        try {
            int affectedRows = productRepository.update(id, prepareUpdatesMap(product));

            if (affectedRows == 1) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).build();
        }
    }

    public ResponseEntity<Void> delete(UUID productId) {
        int deletedCount = productRepository.deleteById(productId);
        if (deletedCount > 0) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private Map<String, Object> prepareUpdatesMap(Product product) {
        Map<String, Object> updates = new LinkedHashMap<>();
        if (product.getTitle() != null) updates.put("title", product.getTitle());
        if (product.getDescription() != null) updates.put("description", product.getDescription());
        if (product.getPhotoUrl() != null) updates.put("photo_url", product.getPhotoUrl());
        if (product.getPrice() != null) updates.put("price", product.getPrice());
        if (product.getCategory() != null) updates.put("category", product.getCategory());
        if (product.getQuantity() != null) updates.put("quantity", product.getQuantity());
        return updates;
    }
}
