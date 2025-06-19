package com.technokratos.eateasy.product.service;

import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.dto.product.ProductUpdateRequest;
import com.technokratos.eateasy.product.entity.Product;
import com.technokratos.eateasy.product.exception.ProductAlreadyExistsException;
import com.technokratos.eateasy.product.exception.ProductNotFoundException;
import com.technokratos.eateasy.product.mapper.ProductMapper;
import com.technokratos.eateasy.product.repository.impl.ProductRepositoryImpl;
import com.technokratos.eateasy.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

  @Mock private ProductRepositoryImpl productRepository;
  @Mock private ProductMapper productMapper;

  @InjectMocks private ProductServiceImpl productService;

  private UUID productId;
  private UUID photoUrlId;
  private Product product;
  private ProductRequest productRequest;
  private ProductUpdateRequest productUpdateRequest;
  private ProductResponse productResponse;

  @BeforeEach
  void setUp() {
    productId = UUID.randomUUID();
    photoUrlId = UUID.randomUUID();

    product = Product.builder()
            .id(productId)
            .title("Burger")
            .description("Tasty burger")
            .photoUrlId(photoUrlId)
            .price(new BigDecimal("5.99"))
            .quantity(10)
            .build();

    productRequest = new ProductRequest();
    productRequest.setTitle("Burger");
    productRequest.setDescription("Tasty burger");
    productRequest.setPrice(new BigDecimal("5.99"));
    productRequest.setQuantity(10);
    productRequest.setPhotoUrlId(photoUrlId);
    productRequest.setCategories(List.of(UUID.randomUUID()));

    productUpdateRequest = new ProductUpdateRequest();
    productUpdateRequest.setTitle("Updated Burger");
    productUpdateRequest.setDescription("More tasty");
    productUpdateRequest.setPrice(new BigDecimal("7.99"));
    productUpdateRequest.setQuantity(15);
    productUpdateRequest.setPhotoUrlId(photoUrlId);

    productResponse = ProductResponse.builder()
            .id(productId)
            .title("Burger")
            .description("Tasty burger")
            .photoUrlId(photoUrlId.toString())
            .price(new BigDecimal("5.99"))
            .quantity(10)
            .build();
  }

  @Test
  void create_ShouldHandlePhotoUrlId() {
    when(productMapper.toEntity(productRequest)).thenReturn(product);
    when(productRepository.save(product)).thenReturn(product);
    when(productMapper.toResponse(product)).thenReturn(productResponse);

    ProductResponse result = productService.create(productRequest);

    assertEquals(photoUrlId.toString(), result.photoUrlId());
  }

  @Test
  void update_ShouldMapPhotoUrlId() {
    when(productMapper.toEntity(productUpdateRequest)).thenReturn(product);
    when(productRepository.update(eq(productId), anyMap())).thenReturn(1);

    productService.update(productId, productUpdateRequest);

    ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
    verify(productRepository).update(eq(productId), captor.capture());

    Map<String, Object> updateParams = captor.getValue();
    assertEquals(photoUrlId, updateParams.get("photoUrlId"));
  }

  @Test
  void getByCategoryId_ShouldHandlePhotoUrlId() {
    UUID categoryId = UUID.randomUUID();
    when(productRepository.getByCategoryId(any(), any(), anyInt(), anyInt(), any(), any()))
            .thenReturn(List.of(product));
    when(productMapper.toResponse(product)).thenReturn(productResponse);

    List<ProductResponse> results = productService.getByCategoryId(
            categoryId, "price", 0, 10, BigDecimal.ZERO, BigDecimal.TEN);

    assertEquals(1, results.size());
    assertEquals(photoUrlId.toString(), results.get(0).photoUrlId());
  }

  @Test
  void update_ShouldHandleEmptyRequest() {
    ProductUpdateRequest emptyRequest = new ProductUpdateRequest();
    when(productMapper.toEntity(emptyRequest)).thenReturn(Product.builder().build());
    when(productRepository.update(eq(productId), anyMap())).thenReturn(0);

    assertThrows(ProductNotFoundException.class,
            () -> productService.update(productId, emptyRequest));
  }

  @Test
  void getByIdSuccess() {
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(productMapper.toResponse(product)).thenReturn(productResponse);
    ProductResponse result = productService.getById(productId);
    assertEquals(photoUrlId.toString(), result.photoUrlId());
  }

  @Test
  void createDuplicateThrowsException() {
    when(productMapper.toEntity(productRequest)).thenReturn(product);
    when(productRepository.save(product)).thenThrow(DataIntegrityViolationException.class);
    assertThrows(ProductAlreadyExistsException.class, () -> productService.create(productRequest));
  }

  @Test
  void updateQuantitySuccess() {
    when(productRepository.updateQuantityIfNotNegative(productId, 5)).thenReturn(1);
    assertDoesNotThrow(() -> productService.updateQuantity(productId, 5));
  }
}