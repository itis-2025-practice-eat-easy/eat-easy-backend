package com.technokratos.eateasy.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.technokratos.eateasy.product.dto.category.CategoryResponse;
import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.dto.product.ProductUpdateRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductCategoryFacadeUnitTest {

  @Mock private ProductService productService;

  @Mock private CategoryService categoryService;

  @InjectMocks private ProductCategoryFacade facade;

  private UUID productId;
  private ProductRequest productRequest;
  private ProductResponse productResponse;
  private ProductUpdateRequest productUpdateRequest;

  @BeforeEach
  void setUp() {
    productId = UUID.randomUUID();

    productRequest =
        ProductRequest.builder()
            .title("Product name")
            .description("Description")
            .photoUrl("http://example.com/image.jpg")
            .price(BigDecimal.valueOf(100))
            .categories(List.of(UUID.randomUUID(), UUID.randomUUID()))
            .quantity(10)
            .build();

    productUpdateRequest =
        ProductUpdateRequest.builder()
            .title("Product name")
            .description("Description")
            .photoUrl("http://example.com/image.jpg")
            .price(BigDecimal.valueOf(100))
            .categories(List.of(UUID.randomUUID(), UUID.randomUUID()))
            .quantity(10)
            .build();

    productResponse =
        ProductResponse.builder()
            .id(productId)
            .title("Product name")
            .description("Description")
            .photoUrl("http://example.com/image.jpg")
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
  void create_ShouldCreateProductAndAssignCategories() {
    List<CategoryResponse> categories =
        List.of(
            new CategoryResponse(productRequest.categories().get(0), "Fruits"),
            new CategoryResponse(productRequest.categories().get(1), "Vegetables"));

    when(productService.create(productRequest)).thenReturn(productResponse);
    when(categoryService.getCategoriesByProductId(productId)).thenReturn(categories);

    ProductResponse result = facade.create(productRequest);

    assertEquals(productId, result.id());
    assertEquals(2, result.categories().size());
    verify(productService).create(productRequest);
    verify(categoryService).assignCategoriesToProduct(productRequest.categories(), productId);
  }

  @Test
  void update_ShouldUpdateCategoriesAndProduct_WhenCategoriesNotEmpty() {
    facade.update(productId, productUpdateRequest);
    verify(categoryService)
        .updateCategoriesByProductId(productUpdateRequest.categories(), productId);
    verify(productService).update(productId, productUpdateRequest);
  }

  @Test
  void update_ShouldOnlyUpdateProduct_WhenCategoriesEmpty() {
    ProductUpdateRequest request =
        ProductUpdateRequest.builder()
            .title("Product")
            .description("Desc")
            .photoUrl("http://example.com/image.jpg")
            .price(BigDecimal.TEN)
            .categories(Collections.emptyList())
            .quantity(5)
            .build();

    facade.update(productId, request);
    verify(categoryService, never()).updateCategoriesByProductId(any(), any());
    verify(productService).update(productId, request);
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
            .photoUrl("http://example.com/image.jpg")
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
