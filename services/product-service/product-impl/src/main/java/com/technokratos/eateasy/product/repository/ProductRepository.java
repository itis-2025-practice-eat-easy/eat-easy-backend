package com.technokratos.eateasy.product.repository;

import com.technokratos.eateasy.product.entity.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
  public Optional<Product> findById(UUID productId);

  public Product save(Product product);

  public int updateQuantityIfNotNegative(UUID productId, Integer quantity);

  public int update(UUID productId, Map<String, Object> updates);

  public int deleteById(UUID productId);

  public List<Product> getByCategoryId(
      UUID categoryId,
      String orderBy,
      Integer page,
      Integer pageSize,
      BigDecimal minPrice,
      BigDecimal maxPrice);

  boolean isProductExist(String title);
}
