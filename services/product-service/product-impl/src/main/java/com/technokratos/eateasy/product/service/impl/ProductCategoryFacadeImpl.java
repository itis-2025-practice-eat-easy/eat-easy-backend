package com.technokratos.eateasy.product.service.impl;

import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.dto.product.ProductUpdateRequest;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductCategoryFacadeImpl implements ProductCategoryFacade {

  private final ProductService productService;
  private final CategoryService categoryService;

  public ProductResponse getById(UUID id) {
    ProductResponse response = productService.getById(id);
    response.categories().addAll(categoryService.getCategoriesByProductId(id));
    return response;
  }

  @Transactional
  public ProductResponse create(ProductRequest productRequest) {
    ProductResponse response = productService.create(productRequest);
    categoryService.assignCategoriesToProduct(productRequest.categories(), response.id());
    response.categories().addAll(categoryService.getCategoriesByProductId(response.id()));
    log.info("Created product with id: {}", response.id());
    return response;
  }

  @Transactional
  public void update(UUID id, ProductUpdateRequest productUpdateRequest) {
    if (!(productUpdateRequest.categories() == null
        || productUpdateRequest.categories().isEmpty())) {
      categoryService.updateCategoriesByProductId(productUpdateRequest.categories(), id);
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
    categoryService.getById(id); // check if category exists
    List<ProductResponse> products =
        productService.getByCategoryId(id, orderBy, page, pageSize, maxPrice, minPrice);
    products.forEach(
        product ->
            product.categories().addAll(categoryService.getCategoriesByProductId(product.id())));
    return products;
  }
}
