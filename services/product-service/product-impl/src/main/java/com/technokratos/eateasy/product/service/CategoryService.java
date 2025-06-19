package com.technokratos.eateasy.product.service;

import com.technokratos.eateasy.product.dto.category.CategoryRequest;
import com.technokratos.eateasy.product.dto.category.CategoryResponse;
import java.util.List;
import java.util.UUID;

public interface CategoryService {
  public void assignCategoriesToProduct(List<UUID> categories, UUID productId);

  public List<CategoryResponse> getAll();

  public CategoryResponse create(CategoryRequest categoryRequest);

  public List<CategoryResponse> getCategoriesByProductId(UUID productId);

  public void updateCategoriesByProductId(List<UUID> categories, UUID productId);

  public CategoryResponse getById(UUID id);
}
