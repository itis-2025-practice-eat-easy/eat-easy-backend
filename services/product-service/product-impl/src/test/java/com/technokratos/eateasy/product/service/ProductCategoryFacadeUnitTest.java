package com.technokratos.eateasy.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.technokratos.eateasy.product.dto.category.CategoryResponse;
import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.dto.product.ProductUpdateRequest;
import com.technokratos.eateasy.product.service.impl.CategoryServiceImpl;
import com.technokratos.eateasy.product.service.impl.ProductCategoryFacadeImpl;
import com.technokratos.eateasy.product.service.impl.ProductServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ProductCategoryFacadeUnitTest {

  @Mock private ProductServiceImpl productService;

  @Mock private CategoryServiceImpl categoryService;

  @InjectMocks private ProductCategoryFacadeImpl facade;

  private UUID productId;
  private ProductRequest productRequest;
  private ProductResponse productResponse;
  private ProductUpdateRequest productUpdateRequest;
  private ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    productId = UUID.randomUUID();

    productRequest =
        ProductRequest.builder()
            .title("Product name")
            .description("Description")
            .photoUrlId(UUID.fromString("f05d7d66-9d1f-4de9-b9b5-e2e6e4b33c91"))
            .price(BigDecimal.valueOf(100))
            .categories(List.of(UUID.randomUUID(), UUID.randomUUID()))
            .quantity(10)
            .build();

    productUpdateRequest =
        ProductUpdateRequest.builder()
            .title("Product name")
            .description("Description")
            .photoUrlId(UUID.fromString("f05d7d66-9d1f-4de9-b9b5-e2e6e4b33c91"))
            .price(BigDecimal.valueOf(100))
            .categories(List.of(UUID.randomUUID(), UUID.randomUUID()))
            .quantity(10)
            .build();

    productResponse =
        ProductResponse.builder()
            .id(productId)
            .title("Product name")
            .description("Description")
            .photoUrlId("f05d7d66-9d1f-4de9-b9b5-e2e6e4b33c91")
            .price(BigDecimal.valueOf(100))
            .categories(new ArrayList<>())
            .quantity(10)
            .createdAt(LocalDateTime.now())
            .popularity(5)
            .build();
  }

  @Test
  void getById_ShouldReturnProductWithCategories() {
    List<CategoryResponse> categories =
        List.of(
            new CategoryResponse(UUID.randomUUID(), "Fruits"),
            new CategoryResponse(UUID.randomUUID(), "Vegetables"));

    when(productService.getById(productId)).thenReturn(productResponse);
    when(categoryService.getCategoriesByProductId(productId)).thenReturn(categories);

    ProductResponse result = facade.getById(productId);

    assertEquals(productId, result.id());
    assertEquals(2, result.categories().size());
    verify(productService).getById(productId);
    verify(categoryService).getCategoriesByProductId(productId);
  }

  @Test
  void create_ShouldCreateProductAndAssignCategories() throws Exception {
    String productJson = objectMapper.writeValueAsString(productRequest);

    List<CategoryResponse> categories =
            List.of(
                    new CategoryResponse(productRequest.getCategories().get(0), "Fruits"),
                    new CategoryResponse(productRequest.getCategories().get(1), "Vegetables"));

    when(productService.create(any(ProductRequest.class))).thenReturn(productResponse);
    when(categoryService.getCategoriesByProductId(productId)).thenReturn(categories);

    ProductResponse result = facade.create(productJson, null);

    assertEquals(productId, result.id());
    assertEquals(2, result.categories().size());

    ArgumentCaptor<ProductRequest> captor = ArgumentCaptor.forClass(ProductRequest.class);
    verify(productService).create(captor.capture());

    ProductRequest actualRequest = captor.getValue();
    assertEquals(productRequest.getTitle(), actualRequest.getTitle());
    assertEquals(productRequest.getDescription(), actualRequest.getDescription());

    verify(categoryService).assignCategoriesToProduct(eq(productRequest.getCategories()), eq(productId));
  }

  @Test
  void update_ShouldUpdateCategoriesAndProduct_WhenCategoriesNotEmpty() throws Exception {
    String updateJson = objectMapper.writeValueAsString(productUpdateRequest);

    facade.update(productId, updateJson, null);

    ArgumentCaptor<ProductUpdateRequest> captor = ArgumentCaptor.forClass(ProductUpdateRequest.class);
    verify(productService).update(eq(productId), captor.capture());

    ProductUpdateRequest actualRequest = captor.getValue();
    assertEquals(productUpdateRequest.getTitle(), actualRequest.getTitle());
    assertEquals(productUpdateRequest.getPrice(), actualRequest.getPrice());

    verify(categoryService)
            .updateCategoriesByProductId(eq(productUpdateRequest.getCategories()), eq(productId));
  }

  @Test
  void update_ShouldOnlyUpdateProduct_WhenCategoriesEmpty() throws Exception {
    ProductUpdateRequest request = ProductUpdateRequest.builder()
            .title("Product")
            .description("Desc")
            .photoUrlId(UUID.fromString("f05d7d66-9d1f-4de9-b9b5-e2e6e4b33c91"))
            .price(BigDecimal.TEN)
            .categories(Collections.emptyList())
            .quantity(5)
            .build();

    String requestJson = objectMapper.writeValueAsString(request);

    facade.update(productId, requestJson, null);

    verify(categoryService, never()).updateCategoriesByProductId(any(), any());
    verify(productService).update(eq(productId), any(ProductUpdateRequest.class));
  }

  @Test
  void getProductsByCategoryId_ShouldReturnProductsWithCategories() {
    UUID categoryId = UUID.randomUUID();
    List<CategoryResponse> categories =
        List.of(
            new CategoryResponse(UUID.randomUUID(), "Fruits"),
            new CategoryResponse(UUID.randomUUID(), "Snacks"));

    ProductResponse product =
        ProductResponse.builder()
            .id(productId)
            .title("Product")
            .description("Desc")
            .photoUrlId("f05d7d66-9d1f-4de9-b9b5-e2e6e4b33c91")
            .price(BigDecimal.TEN)
            .categories(new ArrayList<>())
            .quantity(5)
            .createdAt(LocalDateTime.now())
            .popularity(3)
            .build();

    when(categoryService.getById(categoryId)).thenReturn(new CategoryResponse(categoryId, "Test"));
    when(productService.getByCategoryId(categoryId, "price", 0, 10, BigDecimal.TEN, BigDecimal.ONE))
        .thenReturn(List.of(product));
    when(categoryService.getCategoriesByProductId(productId)).thenReturn(categories);

    List<ProductResponse> result =
        facade.getProductsByCategoryId(categoryId, "price", 0, 10, BigDecimal.TEN, BigDecimal.ONE);

    assertEquals(1, result.size());
    assertEquals(2, result.get(0).categories().size());
    verify(categoryService).getById(categoryId);
    verify(productService)
        .getByCategoryId(categoryId, "price", 0, 10, BigDecimal.TEN, BigDecimal.ONE);
  }
}
