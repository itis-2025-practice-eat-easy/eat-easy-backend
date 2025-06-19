package com.technokratos.eateasy.product.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.eateasy.common.exception.BadRequestServiceException;
import com.technokratos.eateasy.common.exception.ServerErrorServiceException;
import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.dto.product.ProductUpdateRequest;
import com.technokratos.eateasy.product.exception.ProductParsingException;
import com.technokratos.eateasy.product.service.AvatarStorageService;
import com.technokratos.eateasy.product.service.CategoryService;
import com.technokratos.eateasy.product.service.ProductCategoryFacade;
import com.technokratos.eateasy.product.service.ProductService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
  private final Validator validator;

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
      Set<ConstraintViolation<ProductRequest>> violations = validator.validate(productRequest);
      if (!violations.isEmpty()) {
        String errorMessage = violations.stream()
                .map(violation -> String.format(
                        "%s %s (invalid value: %s)",
                        violation.getPropertyPath(),
                        violation.getMessage(),
                        violation.getInvalidValue()))
                .collect(Collectors.joining("; "));
        throw new BadRequestServiceException(errorMessage);
      }
    } catch (JsonProcessingException e) {
      throw new ProductParsingException(e.getMessage());
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
      Set<ConstraintViolation<ProductUpdateRequest>> violations = validator.validate(productUpdateRequest);
      if (!violations.isEmpty()) {
        String errorMessage = violations.stream()
                .map(violation -> String.format(
                        "%s %s (invalid value: %s)",
                        violation.getPropertyPath(),
                        violation.getMessage(),
                        violation.getInvalidValue()))
                .collect(Collectors.joining("; "));
        throw new BadRequestServiceException(errorMessage);
      }
    } catch (JsonProcessingException e) {
      throw new ProductParsingException(e.getMessage());
    }
    productUpdateRequest.setPhotoUrlId(setPhoto(avatarFile));
    if (productUpdateRequest.getCategories() != null && !productUpdateRequest.getCategories().isEmpty()) {
      categoryService.updateCategoriesByProductId(productUpdateRequest.getCategories(), id);
    }
    productService.update(id, productUpdateRequest);
    log.info("Updated product with id: {}", id);
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
