package com.technokratos.eateasy.product.service;

import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.dto.product.ProductUpdateRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductService {
  public ProductResponse getById(UUID id);

  public ProductResponse create(ProductRequest product);

  public void updateQuantity(UUID id, Integer quantity);

  public void update(UUID id, ProductUpdateRequest product);

  public void delete(UUID id);

  public List<ProductResponse> getByCategoryId(
      UUID id,
      String orderBy,
      Integer page,
      Integer pageSize,
      BigDecimal maxPrice,
      BigDecimal minPrice);
}
