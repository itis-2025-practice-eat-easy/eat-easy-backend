package com.smesitejl.product.service;

import com.smesitejl.product.entity.Product;
import com.smesitejl.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ResponseEntity<Product> getById(UUID productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if(product != null){
            return ResponseEntity.ok(product);
        }
        else{
            return ResponseEntity.notFound().build();
        }

    }

    public ResponseEntity<Product> create(Product product) {
        try{
            Product savedProduct = productRepository.save(product);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedProduct.getId())
                    .toUri();

            return ResponseEntity
                    .created((location))
                    .body(product);
        }
        catch (DataIntegrityViolationException e){
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
        Product existingProduct = productRepository.findById(id)
                .orElse(null);
        if (existingProduct == null){
            return ResponseEntity.notFound().build();
        }

        if (product.getTitle() != null) {
            existingProduct.setTitle(product.getTitle());
        }
        if (product.getDescription() != null) {
            existingProduct.setDescription(product.getDescription());
        }
        if (product.getPhotoUrl() != null) {
            existingProduct.setPhotoUrl(product.getPhotoUrl());
        }
        if (product.getPrice() != null) {
            existingProduct.setPrice(product.getPrice());
        }
        if (product.getCategory() != null) {
            existingProduct.setCategory(product.getCategory());
        }
        if (product.getQuantity() != null) {
            existingProduct.setQuantity(product.getQuantity());
        }

        try{
            productRepository.save(existingProduct);
            return ResponseEntity.ok().build();
        }
        catch (RuntimeException e){
            return ResponseEntity.badRequest().body(null);
        }

    }

    public ResponseEntity<Void> delete(UUID productId) {
        int deletedCount = productRepository.deleteProductById(productId);

        if (deletedCount > 0) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
