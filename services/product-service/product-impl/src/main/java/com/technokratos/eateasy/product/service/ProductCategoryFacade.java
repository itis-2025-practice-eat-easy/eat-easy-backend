package com.technokratos.eateasy.product.service;

import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.dto.product.ProductUpdateRequest;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductCategoryFacade {
  public ProductResponse getById(UUID id);

  public ProductResponse create(String product, MultipartFile avatarFile);

  public void update(UUID id, String product, MultipartFile avatarFile);

  public List<ProductResponse> getProductsByCategoryId(
      UUID id,
      String orderBy,
      Integer page,
      Integer pageSize,
      BigDecimal maxPrice,
      BigDecimal minPrice);
}
