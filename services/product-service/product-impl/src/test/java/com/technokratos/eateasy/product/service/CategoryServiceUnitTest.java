package com.technokratos.eateasy.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.technokratos.eateasy.product.dto.category.CategoryRequest;
import com.technokratos.eateasy.product.dto.category.CategoryResponse;
import com.technokratos.eateasy.product.entity.Category;
import com.technokratos.eateasy.product.exception.CategoryAlreadyExistsException;
import com.technokratos.eateasy.product.exception.CategoryNotFoundException;
import com.technokratos.eateasy.product.mapper.CategoryMapper;
import com.technokratos.eateasy.product.repository.impl.CategoryRepositoryImpl;
import com.technokratos.eateasy.product.service.impl.CategoryServiceImpl;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class CategoryServiceUnitTest {

  @Mock private CategoryRepositoryImpl categoryRepository;

  @Mock private CategoryMapper categoryMapper;

  @InjectMocks private CategoryServiceImpl categoryService;

  private Category category;
  private CategoryRequest categoryRequest;
  private CategoryResponse categoryResponse;

  @BeforeEach
  void setUp() {
    UUID id = UUID.randomUUID();
    category = Category.builder().id(id).title("Drinks").build();

    categoryRequest = new CategoryRequest("Drinks");

    categoryResponse = new CategoryResponse(id, "Drinks");
  }

  @Test
  void getAllCategoriesTest() {
    when(categoryRepository.findAll()).thenReturn(List.of(category));
    when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

    List<CategoryResponse> result = categoryService.getAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(categoryResponse, result.get(0));
  }

  @Test
  void getCategoryByIdSuccessTest() {
    UUID id = category.getId();
    when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
    when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

    CategoryResponse result = categoryService.getById(id);

    assertNotNull(result);
    assertEquals(categoryResponse, result);
  }

  @Test
  void getCategoryByIdNotFoundTest() {
    UUID id = UUID.randomUUID();
    when(categoryRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(CategoryNotFoundException.class, () -> categoryService.getById(id));
  }

  @Test
  void createCategorySuccessTest() {
    when(categoryMapper.toEntity(categoryRequest)).thenReturn(category);
    when(categoryRepository.save(category)).thenReturn(category);
    when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

    CategoryResponse result = categoryService.create(categoryRequest);

    assertNotNull(result);
    assertEquals(categoryResponse, result);
  }

  @Test
  void createCategoryAlreadyExistsTest() {
    when(categoryMapper.toEntity(categoryRequest)).thenReturn(category);
    when(categoryRepository.save(category)).thenThrow(DataIntegrityViolationException.class);

    assertThrows(
        CategoryAlreadyExistsException.class, () -> categoryService.create(categoryRequest));
  }

  @Test
  void getCategoriesByProductIdTest() {
    UUID productId = UUID.randomUUID();
    when(categoryRepository.getCategoriesByProductId(productId)).thenReturn(List.of(category));

    List<CategoryResponse> result = categoryService.getCategoriesByProductId(productId);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(category.getId(), result.get(0).id());
    assertEquals(category.getTitle(), result.get(0).title());
  }

  @Test
  void assignCategoriesToProductTest() {
    List<UUID> categoryIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    UUID productId = UUID.randomUUID();

    categoryService.assignCategoriesToProduct(categoryIds, productId);

    verify(categoryRepository, times(1)).assignCategoriesToProduct(categoryIds, productId);
  }

  @Test
  void updateCategoriesByProductIdTest() {
    List<UUID> categoryIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    UUID productId = UUID.randomUUID();

    categoryService.updateCategoriesByProductId(categoryIds, productId);

    verify(categoryRepository, times(1)).updateCategoriesByProductId(categoryIds, productId);
  }
}
