package com.technokratos.eateasy.product.service;

import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.dto.product.ProductUpdateRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductCategoryFacade {
  public ProductResponse getById(UUID id);

  public ProductResponse create(ProductRequest productRequest);

  public void update(UUID id, ProductUpdateRequest productUpdateRequest);

  public List<ProductResponse> getProductsByCategoryId(
      UUID id,
      String orderBy,
      Integer page,
      Integer pageSize,
      BigDecimal maxPrice,
      BigDecimal minPrice);
}
