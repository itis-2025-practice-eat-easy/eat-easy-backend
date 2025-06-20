package com.technokratos.eateasy.product.service.impl;

import com.technokratos.eateasy.product.dto.category.CategoryRequest;
import com.technokratos.eateasy.product.dto.category.CategoryResponse;
import com.technokratos.eateasy.product.entity.Category;
import com.technokratos.eateasy.product.exception.CategoryAlreadyExistsException;
import com.technokratos.eateasy.product.exception.CategoryNotFoundException;
import com.technokratos.eateasy.product.mapper.CategoryMapper;
import com.technokratos.eateasy.product.repository.CategoryRepository;
import com.technokratos.eateasy.product.service.CategoryService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;

  private final CategoryMapper categoryMapper;

  public void assignCategoriesToProduct(List<UUID> categories, UUID productId) {
    categoryRepository.assignCategoriesToProduct(categories, productId);
  }

  public List<CategoryResponse> getAll() {
    return categoryRepository.findAll().stream()
        .map(categoryMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public CategoryResponse create(CategoryRequest categoryRequest) {
    try {
      log.info("Create category: {}", categoryRequest);
      return categoryMapper.toResponse(
          categoryRepository.save(categoryMapper.toEntity(categoryRequest)));
    } catch (DataIntegrityViolationException e) {
      log.warn("Category already exists: {}", categoryRequest);
      throw new CategoryAlreadyExistsException(
          "Category already exist with title %s".formatted(categoryRequest.title()));
    }
  }

  public List<CategoryResponse> getCategoriesByProductId(UUID productId) {
    List<Category> categories = categoryRepository.getCategoriesByProductId(productId);
    return categories.stream()
        .map(category -> new CategoryResponse(category.getId(), category.getTitle()))
        .collect(Collectors.toList());
  }

  public void updateCategoriesByProductId(List<UUID> categories, UUID productId) {
    categoryRepository.updateCategoriesByProductId(categories, productId);
  }

  public CategoryResponse getById(UUID id) {
    return categoryRepository
        .findById(id)
        .map(categoryMapper::toResponse)
        .orElseThrow(
            () -> new CategoryNotFoundException("Category not found with id %s".formatted(id)));
  }
}
