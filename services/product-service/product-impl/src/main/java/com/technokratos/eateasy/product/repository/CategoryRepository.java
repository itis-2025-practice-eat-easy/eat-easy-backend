package com.technokratos.eateasy.product.repository;

import com.technokratos.eateasy.product.entity.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {
  public void assignCategoriesToProduct(List<UUID> categoriesId, UUID productId);

  public List<Category> getCategoriesByProductId(UUID productId);

  public Category save(Category category);

  public List<Category> findAll();

  public Optional<Category> findById(UUID id);

  public void updateCategoriesByProductId(List<UUID> categories, UUID productId);
}
