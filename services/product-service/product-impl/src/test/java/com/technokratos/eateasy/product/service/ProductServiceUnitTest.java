package com.technokratos.eateasy.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.dto.product.ProductUpdateRequest;
import com.technokratos.eateasy.product.entity.Product;
import com.technokratos.eateasy.product.exception.ProductAlreadyExistsException;
import com.technokratos.eateasy.product.exception.ProductNotFoundException;
import com.technokratos.eateasy.product.mapper.ProductMapper;
import com.technokratos.eateasy.product.repository.impl.ProductRepositoryImpl;
import com.technokratos.eateasy.product.service.impl.ProductServiceImpl;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

  @Mock private ProductRepositoryImpl productRepository;

  @Mock private ProductMapper productMapper;

  @InjectMocks private ProductServiceImpl productService;

  private Product product;
  private ProductRequest productRequest;
  private ProductUpdateRequest productUpdateRequest;
  private ProductResponse productResponse;
  private UUID productId;

  @BeforeEach
  void setUp() {
    productId = UUID.randomUUID();

    product =
        Product.builder()
            .id(productId)
            .title("Burger")
            .description("Tasty burger")
            .photoUrl("http://image.com")
            .price(new BigDecimal("5.99"))
            .quantity(10)
            .build();

    productRequest =
        ProductRequest.builder()
            .title("Burger")
            .description("Tasty burger")
            .photoUrl("http://image.com")
            .price(new BigDecimal("5.99"))
            .quantity(10)
            .build();

    productUpdateRequest =
        ProductUpdateRequest.builder()
            .title("Burger")
            .description("Tasty burger")
            .photoUrl("http://image.com")
            .price(new BigDecimal("5.99"))
            .quantity(10)
            .build();

    productResponse =
        ProductResponse.builder()
            .id(productId)
            .title("Burger")
            .description("Tasty burger")
            .photoUrl("http://image.com")
            .price(new BigDecimal("5.99"))
            .quantity(10)
            .build();
  }

  @Test
  void getByIdSuccess() {
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(productMapper.toResponse(product)).thenReturn(productResponse);
    ProductResponse result = productService.getById(productId);
    assertNotNull(result);
    assertEquals(productResponse, result);
  }

  @Test
  void getByIdNotFound() {
    when(productRepository.findById(productId)).thenReturn(Optional.empty());
    assertThrows(ProductNotFoundException.class, () -> productService.getById(productId));
  }

  @Test
  void createSuccess() {
    when(productMapper.toEntity(productRequest)).thenReturn(product);
    when(productRepository.save(product)).thenReturn(product);
    when(productMapper.toResponse(product)).thenReturn(productResponse);
    ProductResponse result = productService.create(productRequest);
    assertNotNull(result);
    assertEquals(productResponse, result);
  }

  @Test
  void createDuplicateThrowsException() {
    when(productMapper.toEntity(productRequest)).thenReturn(product);
    when(productRepository.save(product)).thenThrow(DataIntegrityViolationException.class);
    assertThrows(ProductAlreadyExistsException.class, () -> productService.create(productRequest));
  }

  @Test
  void createRuntimeExceptionFromRepository() {
    when(productMapper.toEntity(productRequest)).thenReturn(product);
    when(productRepository.save(product)).thenThrow(new RuntimeException("Unexpected"));
    assertThrows(RuntimeException.class, () -> productService.create(productRequest));
  }

  @Test
  void updateQuantitySuccess() {
    when(productRepository.updateQuantityIfNotNegative(productId, 5)).thenReturn(1);
    assertDoesNotThrow(() -> productService.updateQuantity(productId, 5));
  }

  @Test
  void updateQuantityNotFound() {
    when(productRepository.updateQuantityIfNotNegative(productId, 5)).thenReturn(0);
    assertThrows(ProductNotFoundException.class, () -> productService.updateQuantity(productId, 5));
  }

  @Test
  void updateQuantityDataViolation() {
    when(productRepository.updateQuantityIfNotNegative(productId, 5))
        .thenThrow(DataIntegrityViolationException.class);
    assertThrows(
        DataIntegrityViolationException.class, () -> productService.updateQuantity(productId, 5));
  }

  @Test
  void updateQuantityRuntimeException() {
    when(productRepository.updateQuantityIfNotNegative(productId, 5))
        .thenThrow(new RuntimeException("unexpected"));
    assertThrows(RuntimeException.class, () -> productService.updateQuantity(productId, 5));
  }

  @Test
  void updateSuccess() {
    when(productMapper.toEntity(productUpdateRequest)).thenReturn(product);
    when(productRepository.update(eq(productId), anyMap())).thenReturn(1);
    assertDoesNotThrow(() -> productService.update(productId, productUpdateRequest));
  }

  @Test
  void updateNotFound() {
    when(productMapper.toEntity(productUpdateRequest)).thenReturn(product);
    when(productRepository.update(eq(productId), anyMap())).thenReturn(0);
    assertThrows(
        ProductNotFoundException.class,
        () -> productService.update(productId, productUpdateRequest));
  }

  @Test
  void updateDataViolation() {
    when(productMapper.toEntity(productUpdateRequest)).thenReturn(product);
    when(productRepository.update(eq(productId), anyMap()))
        .thenThrow(DataIntegrityViolationException.class);
    assertThrows(
        DataIntegrityViolationException.class,
        () -> productService.update(productId, productUpdateRequest));
  }

  @Test
  void updateRuntimeException() {
    when(productMapper.toEntity(productRequest)).thenReturn(product);
    when(productRepository.update(eq(productId), anyMap()))
        .thenThrow(new RuntimeException("unexpected"));
    assertThrows(
        RuntimeException.class, () -> productService.update(productId, productUpdateRequest));
  }

  @Test
  void deleteSuccess() {
    when(productRepository.deleteById(productId)).thenReturn(1);
    assertDoesNotThrow(() -> productService.delete(productId));
  }

  @Test
  void deleteNotFound() {
    when(productRepository.deleteById(productId)).thenReturn(0);
    assertThrows(ProductNotFoundException.class, () -> productService.delete(productId));
  }

  @Test
  void deleteRuntimeException() {
    when(productRepository.deleteById(productId)).thenThrow(new RuntimeException("fail"));
    assertThrows(RuntimeException.class, () -> productService.delete(productId));
  }

  @Test
  void getByCategoryId_allFiltersAndSort_price() {
    UUID categoryId = UUID.randomUUID();
    when(productRepository.getByCategoryId(
            eq(categoryId),
            eq("price"),
            eq(0),
            eq(10),
            eq(new BigDecimal("2.00")),
            eq(new BigDecimal("10.00"))))
        .thenReturn(List.of(product));
    when(productMapper.toResponse(product)).thenReturn(productResponse);
    List<ProductResponse> result =
        productService.getByCategoryId(
            categoryId, "price", 0, 10, new BigDecimal("2.00"), new BigDecimal("10.00"));
    assertEquals(1, result.size());
  }

  @Test
  void getByCategoryId_allFiltersAndSort_popularity() {
    UUID categoryId = UUID.randomUUID();
    when(productRepository.getByCategoryId(
            eq(categoryId), eq("popularity"), eq(0), eq(10), eq(null), eq(null)))
        .thenReturn(List.of(product));
    when(productMapper.toResponse(product)).thenReturn(productResponse);
    List<ProductResponse> result =
        productService.getByCategoryId(categoryId, "popularity", 0, 10, null, null);
    assertEquals(1, result.size());
  }

  @Test
  void getByCategoryId_sortByNew() {
    UUID categoryId = UUID.randomUUID();
    when(productRepository.getByCategoryId(
            eq(categoryId), eq("new"), eq(0), eq(10), eq(null), eq(null)))
        .thenReturn(List.of(product));
    when(productMapper.toResponse(product)).thenReturn(productResponse);
    List<ProductResponse> result =
        productService.getByCategoryId(categoryId, "new", 0, 10, null, null);
    assertEquals(1, result.size());
  }

  @Test
  void updateEmptyRequest_doesNothing() {
    when(productMapper.toEntity(productUpdateRequest))
        .thenReturn(Product.builder().id(productId).build());
    when(productRepository.update(eq(productId), anyMap())).thenReturn(0);
    assertThrows(
        ProductNotFoundException.class,
        () -> productService.update(productId, productUpdateRequest));
  }
}
