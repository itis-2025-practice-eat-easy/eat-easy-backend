package com.technokratos.eateasy.product.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.eateasy.common.exception.BadRequestServiceException;
import com.technokratos.eateasy.product.dto.category.CategoryResponse;
import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.dto.product.ProductUpdateRequest;
import com.technokratos.eateasy.product.service.impl.CategoryServiceImpl;
import com.technokratos.eateasy.product.service.impl.ProductCategoryFacadeImpl;
import com.technokratos.eateasy.product.service.impl.ProductServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCategoryFacadeUnitTest {

  @Mock private ProductServiceImpl productService;
  @Mock private CategoryServiceImpl categoryService;
  @Mock private AvatarStorageService avatarStorageService;
  @Mock private ObjectMapper objectMapper;
  @Mock private Validator validator;

  @InjectMocks private ProductCategoryFacadeImpl facade;

  private UUID productId;
  private UUID photoUrlId;
  private MultipartFile avatarFile;
  private ProductRequest productRequest;
  private ProductUpdateRequest productUpdateRequest;
  private ProductResponse productResponse;

  @BeforeEach
  void setUp() {
    productId = UUID.randomUUID();
    photoUrlId = UUID.randomUUID();
    avatarFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());

    productRequest = new ProductRequest();
    productRequest.setTitle("Product name");
    productRequest.setDescription("Description");
    productRequest.setPrice(BigDecimal.valueOf(100));
    productRequest.setCategories(List.of(UUID.randomUUID(), UUID.randomUUID()));
    productRequest.setQuantity(10);

    productUpdateRequest = new ProductUpdateRequest();
    productUpdateRequest.setTitle("Updated name");
    productUpdateRequest.setDescription("Updated description");
    productUpdateRequest.setPrice(BigDecimal.valueOf(200));
    productUpdateRequest.setCategories(List.of(UUID.randomUUID()));
    productUpdateRequest.setQuantity(20);

    productResponse = ProductResponse.builder()
            .id(productId)
            .title("Product name")
            .description("Description")
            .photoUrlId(photoUrlId.toString())
            .price(BigDecimal.valueOf(100))
            .categories(new ArrayList<>())
            .quantity(10)
            .createdAt(LocalDateTime.now())
            .popularity(5)
            .build();
  }

  @Test
  void create_ShouldCreateProductWithPhoto() throws Exception {
    String json = "product_json";
    when(objectMapper.readValue(json, ProductRequest.class)).thenReturn(productRequest);
    when(validator.validate(productRequest)).thenReturn(Collections.emptySet());
    when(avatarStorageService.uploadAvatar(avatarFile)).thenReturn(photoUrlId);
    when(productService.create(productRequest)).thenReturn(productResponse);

    ProductResponse result = facade.create(json, avatarFile);

    assertEquals(photoUrlId.toString(), result.photoUrlId());
    verify(avatarStorageService).uploadAvatar(avatarFile);
    verify(categoryService).assignCategoriesToProduct(productRequest.getCategories(), productId);
  }

  @Test
  void create_ShouldThrowBadRequestWhenValidationFails() throws Exception {
    String json = "product_json";
    when(objectMapper.readValue(json, ProductRequest.class)).thenReturn(productRequest);

    ConstraintViolation<ProductRequest> violation = new ConstraintViolation<>() {
      @Override
      public String getMessage() {
        return "Invalid title";
      }

      @Override
      public String getMessageTemplate() {
        return null;
      }

      @Override
      public ProductRequest getRootBean() {
        return productRequest;
      }

      @Override
      public Class<ProductRequest> getRootBeanClass() {
        return ProductRequest.class;
      }

      @Override
      public Object getLeafBean() {
        return null;
      }

      @Override
      public Object[] getExecutableParameters() {
        return new Object[0];
      }

      @Override
      public Object getExecutableReturnValue() {
        return null;
      }

      @Override
      public Path getPropertyPath() {
        return PathImpl.createPathFromString("title");
      }

      @Override
      public Object getInvalidValue() {
        return "A";
      }

      @Override
      public ConstraintDescriptor<?> getConstraintDescriptor() {
        return null;
      }

      @Override
      public <U> U unwrap(Class<U> type) {
        return null;
      }
    };

    Set<ConstraintViolation<ProductRequest>> violations = new HashSet<>();
    violations.add(violation);

    when(validator.validate(productRequest)).thenReturn(violations);

    assertThrows(BadRequestServiceException.class, () -> facade.create(json, avatarFile));
  }

  @Test
  void update_ShouldUpdateWithPhotoAndCategories() throws Exception {
    String json = "update_json";
    when(objectMapper.readValue(json, ProductUpdateRequest.class)).thenReturn(productUpdateRequest);
    when(validator.validate(productUpdateRequest)).thenReturn(Collections.emptySet());
    when(avatarStorageService.uploadAvatar(avatarFile)).thenReturn(photoUrlId);

    facade.update(productId, json, avatarFile);

    verify(avatarStorageService).uploadAvatar(avatarFile);
    verify(categoryService).updateCategoriesByProductId(productUpdateRequest.getCategories(), productId);

    ArgumentCaptor<ProductUpdateRequest> captor = ArgumentCaptor.forClass(ProductUpdateRequest.class);
    verify(productService).update(eq(productId), captor.capture());
    assertEquals(photoUrlId, captor.getValue().getPhotoUrlId());
  }

  @Test
  void update_ShouldSkipCategoriesWhenNull() throws Exception {
    String json = "update_json";
    productUpdateRequest.setCategories(null);
    when(objectMapper.readValue(json, ProductUpdateRequest.class)).thenReturn(productUpdateRequest);
    when(validator.validate(productUpdateRequest)).thenReturn(Collections.emptySet());

    facade.update(productId, json, avatarFile);

    verify(categoryService, never()).updateCategoriesByProductId(any(), any());
    verify(productService).update(eq(productId), any(ProductUpdateRequest.class));
  }

  @Test
  void getById_ShouldEnrichWithCategories() {
    List<CategoryResponse> categories = List.of(
            new CategoryResponse(UUID.randomUUID(), "Fruits")
    );
    when(productService.getById(productId)).thenReturn(productResponse);
    when(categoryService.getCategoriesByProductId(productId)).thenReturn(categories);

    ProductResponse result = facade.getById(productId);

    assertEquals(1, result.categories().size());
    assertEquals("Fruits", result.categories().get(0).title());
  }

  @Test
  void getProductsByCategoryId_ShouldEnrichEachProduct() {
    UUID categoryId = UUID.randomUUID();
    List<CategoryResponse> categories = List.of(
            new CategoryResponse(categoryId, "Test")
    );
    when(categoryService.getById(categoryId)).thenReturn(new CategoryResponse(categoryId, "Test"));
    when(productService.getByCategoryId(any(), any(), anyInt(), anyInt(), any(), any()))
            .thenReturn(List.of(productResponse));
    when(categoryService.getCategoriesByProductId(productId)).thenReturn(categories);

    List<ProductResponse> results = facade.getProductsByCategoryId(
            categoryId, "price", 0, 10, BigDecimal.TEN, BigDecimal.ONE);

    assertEquals(1, results.size());
    assertEquals(1, results.get(0).categories().size());
  }
}