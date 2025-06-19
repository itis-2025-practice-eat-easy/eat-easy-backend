package com.technokratos.eateasy.product.controller;

import com.technokratos.eateasy.product.api.CategoryApi;
import com.technokratos.eateasy.product.dto.category.CategoryRequest;
import com.technokratos.eateasy.product.dto.category.CategoryResponse;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.service.CategoryService;
import com.technokratos.eateasy.product.service.ProductCategoryFacade;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CategoryController implements CategoryApi {

  private final CategoryService categoryService;
  private final ProductCategoryFacade productCategoryFacade;

  @Override
  public List<CategoryResponse> getAll() {
    log.info("Received request to get all categories");
    return categoryService.getAll();
  }

  @Override
  public CategoryResponse create(CategoryRequest category) {
    log.info("Received request to create a category {}", category);
    return categoryService.create(category);
  }

  @Override
  public List<ProductResponse> getProductsByCategory(
      UUID id,
      String orderBy,
      Integer page,
      Integer pageSize,
      BigDecimal maxPrice,
      BigDecimal minPrice) {
    return productCategoryFacade.getProductsByCategoryId(
        id, orderBy, page, pageSize, maxPrice, minPrice);
  }
}
