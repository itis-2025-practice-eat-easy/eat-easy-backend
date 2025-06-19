package com.technokratos.eateasy.product.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.dto.product.ProductUpdateRequest;
import com.technokratos.eateasy.product.service.AvatarStorageService;
import com.technokratos.eateasy.product.service.CategoryService;
import com.technokratos.eateasy.product.service.ProductCategoryFacade;
import com.technokratos.eateasy.product.service.ProductService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductCategoryFacadeImpl implements ProductCategoryFacade {

  private final ProductService productService;
  private final CategoryService categoryService;
  private final AvatarStorageService avatarStorageService;
  private final ObjectMapper objectMapper;

  public ProductResponse getById(UUID id) {
    ProductResponse response = productService.getById(id);
    response.categories().addAll(categoryService.getCategoriesByProductId(id));
    return response;
  }

  @Transactional
  public ProductResponse create(String product, MultipartFile avatarFile) {
    ProductRequest productRequest = null;
    try {
      productRequest = objectMapper.readValue(product, ProductRequest.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    productRequest.setPhotoUrlId(setPhoto(avatarFile));
    ProductResponse response = productService.create(productRequest);
    categoryService.assignCategoriesToProduct(productRequest.getCategories(), response.id());
    response.categories().addAll(categoryService.getCategoriesByProductId(response.id()));
    log.info("Created product with id: {}", response.id());
    return response;
  }

  @Transactional
  public void update(UUID id, String product, MultipartFile avatarFile) {
    ProductUpdateRequest productUpdateRequest = null;
    try {
      productUpdateRequest = objectMapper.readValue(product, ProductUpdateRequest.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    productUpdateRequest.setPhotoUrlId(setPhoto(avatarFile));
    if (!(productUpdateRequest.getCategories() == null
        || productUpdateRequest.getCategories().isEmpty())) {
      categoryService.updateCategoriesByProductId(productUpdateRequest.getCategories(), id);
    }
    log.info("Updated product with id: {}", id);
    productService.update(id, productUpdateRequest);
  }

  public List<ProductResponse> getProductsByCategoryId(
      UUID id,
      String orderBy,
      Integer page,
      Integer pageSize,
      BigDecimal maxPrice,
      BigDecimal minPrice) {
    categoryService.getById(id);
    List<ProductResponse> products =
        productService.getByCategoryId(id, orderBy, page, pageSize, maxPrice, minPrice);
    products.forEach(
        product ->
            product.categories().addAll(categoryService.getCategoriesByProductId(product.id())));
    return products;
  }
  private UUID setPhoto(MultipartFile avatarFile){
    UUID photoId = null;
    if (avatarFile != null && !avatarFile.isEmpty()){
      photoId = avatarStorageService.uploadAvatar(avatarFile);
    }
    return photoId;
  }
}
