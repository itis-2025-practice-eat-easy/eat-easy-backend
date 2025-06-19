package com.technokratos.eateasy.product.service.impl;

import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.dto.product.ProductUpdateRequest;
import com.technokratos.eateasy.product.entity.Product;
import com.technokratos.eateasy.product.exception.ProductAlreadyExistsException;
import com.technokratos.eateasy.product.exception.ProductNotFoundException;
import com.technokratos.eateasy.product.mapper.ProductMapper;
import com.technokratos.eateasy.product.repository.ProductRepository;
import com.technokratos.eateasy.product.service.ProductService;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;

  private final ProductMapper productMapper;

  public ProductResponse getById(UUID id) {
    return productRepository
        .findById(id)
        .map(productMapper::toResponse)
        .orElseThrow(
            () -> new ProductNotFoundException("Product not found with id %s".formatted(id)));
  }

  @Transactional
  public ProductResponse create(ProductRequest product) {
    try {
      log.info("Create product {}", product);
      return productMapper.toResponse(productRepository.save(productMapper.toEntity(product)));
    } catch (DataIntegrityViolationException e) {
      log.error("Duplicate product {}", product);
      throw new ProductAlreadyExistsException(
          "Product already exists: %s".formatted(e.getMessage()));
    }
  }

  @Transactional
  public void updateQuantity(UUID id, Integer quantity) {
    try {
      int affectedRows = productRepository.updateQuantityIfNotNegative(id, quantity);
      log.info("Updated product with id: {}", id);
      if (affectedRows == 0) {
        log.info("Product with id: {} not found", id);
        throw new ProductNotFoundException("Product not found with id %s".formatted(id));
      }
    } catch (DataIntegrityViolationException e) {
      log.error("Invalid product data with id {}", id);
      throw new DataIntegrityViolationException(
          "Data integrity violation".formatted(e.getMessage()));
    }
  }

  @Transactional
  public void update(UUID id, ProductUpdateRequest product) {
    try {
      int affectedRows =
          productRepository.update(id, prepareUpdatesMap(productMapper.toEntity(product)));
      log.info("Updated product with id: {}", id);
      if (affectedRows == 0) {
        log.info("Product with id: {} not found", id);
        throw new ProductNotFoundException("Product not found with id %s".formatted(id));
      }
    } catch (DataIntegrityViolationException e) {
      log.error("Invalid product data with id {}", id);
      throw new DataIntegrityViolationException("Data integrity violation");
    }
  }

  @Transactional
  public void delete(UUID id) {
    log.info("Delete product with id: {}", id);
    if (productRepository.deleteById(id) == 0) {
      log.info("Product with id: {} not found", id);
      throw new ProductNotFoundException("Product not found with id %s".formatted(id));
    }
  }

  public List<ProductResponse> getByCategoryId(
      UUID id,
      String orderBy,
      Integer page,
      Integer pageSize,
      BigDecimal maxPrice,
      BigDecimal minPrice) {

    return productRepository
        .getByCategoryId(id, orderBy, page, pageSize, maxPrice, minPrice)
        .stream()
        .map(productMapper::toResponse)
        .collect(Collectors.toList());
  }

  private Map<String, Object> prepareUpdatesMap(Product product) {
    Map<String, Object> updates = new LinkedHashMap<>();
    if (product.getTitle() != null) updates.put("title", product.getTitle());
    if (product.getDescription() != null) updates.put("description", product.getDescription());
    if (product.getPhotoUrl() != null) updates.put("photo_url", product.getPhotoUrl());
    if (product.getPrice() != null) updates.put("price", product.getPrice());
    if (product.getQuantity() != null) updates.put("quantity", product.getQuantity());
    return updates;
  }
}
