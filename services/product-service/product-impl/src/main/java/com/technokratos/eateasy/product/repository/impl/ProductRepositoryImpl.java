package com.technokratos.eateasy.product.repository.impl;

import com.technokratos.eateasy.product.entity.Product;
import com.technokratos.eateasy.product.exception.ProductDataIntegrityViolationException;
import com.technokratos.eateasy.product.exception.ProductDatabaseException;
import com.technokratos.eateasy.product.repository.ProductRepository;
import com.technokratos.eateasy.product.util.QueryProvider;
import java.math.BigDecimal;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

  private final JdbcTemplate jdbcTemplate;
  private final QueryProvider queryProvider;
  private static final Set<String> allowedColumns =
      Set.of("title", "description",  "price", "category", "quantity", "photo_url_id");

  private final RowMapper<Product> productRowMapper =
      (rs, rowNum) ->
          Product.builder()
              .id(UUID.fromString(rs.getString("id")))
              .title(rs.getString("title"))
              .description(rs.getString("description"))
              .photoUrlId(UUID.fromString(rs.getString("photo_url_id")))
              .price(rs.getBigDecimal("price"))
              .quantity(rs.getInt("quantity"))
              .createdAt(rs.getTimestamp("created_at"))
              .popularity(rs.getInt("popularity"))
              .build();

  public Optional<Product> findById(UUID productId) {
    String sql = queryProvider.getSqlQueryForProduct("find_by_id");
    List<Product> results = jdbcTemplate.query(sql, productRowMapper, productId);
    return results.stream().findFirst();
  }

  public Product save(Product product) {
    String sql = queryProvider.getSqlQueryForProduct("save");
    try {
      return jdbcTemplate.queryForObject(
          sql,
          productRowMapper,
          product.getTitle(),
          product.getDescription(),
          product.getPrice(),
          product.getQuantity(),
          product.getCreatedAt(),
          product.getPopularity(),
          product.getPhotoUrlId());
    } catch (DataIntegrityViolationException e) {
      throw new ProductDataIntegrityViolationException(e.getMessage());
    } catch (Exception e) {
      throw new ProductDatabaseException(e.getMessage());
    }
  }

  public int updateQuantityIfNotNegative(UUID productId, Integer quantity) {
    String sql = queryProvider.getSqlQueryForProduct("update_quantity");
    try {
      return jdbcTemplate.update(sql, productId, quantity);
    } catch (DataIntegrityViolationException e) {
      throw new ProductDataIntegrityViolationException(e.getMessage());
    } catch (Exception e) {
      throw new ProductDatabaseException(e.getMessage());
    }
  }

  public int update(UUID productId, Map<String, Object> updates) {
    if (updates.isEmpty()) return 0;

    StringBuilder sql = new StringBuilder("UPDATE product SET ");
    List<Object> params = new ArrayList<>();

    for (Map.Entry<String, Object> entry : updates.entrySet()) {
      String column = entry.getKey();
      if (!allowedColumns.contains(column)) {
        throw new IllegalArgumentException("Invalid column name: %s".formatted(column));
      }
      sql.append(column).append(" = ?, ");
      params.add(entry.getValue());
    }

    sql.setLength(sql.length() - 2);
    sql.append(" WHERE id = ?");
    params.add(productId);

    try {
      return jdbcTemplate.update(sql.toString(), params.toArray());
    } catch (DataIntegrityViolationException e) {
      throw new ProductDataIntegrityViolationException(e.getMessage());
    } catch (Exception e) {
      throw new ProductDatabaseException(e.getMessage());
    }
  }

  public int deleteById(UUID productId) {
    String sql = queryProvider.getSqlQueryForProduct("delete_by_id");
    try {
      return jdbcTemplate.update(sql, productId);
    } catch (Exception e) {
      throw new ProductDatabaseException(e.getMessage());
    }
  }

  public List<Product> getByCategoryId(
      UUID categoryId,
      String orderBy,
      Integer page,
      Integer pageSize,
      BigDecimal minPrice,
      BigDecimal maxPrice) {
    StringBuilder sql = new StringBuilder(queryProvider.getSqlQueryForProduct("find_by_category"));
    List<Object> params = new ArrayList<>();
    params.add(categoryId);

    if (minPrice != null) {
      sql.append(" AND price >= ?");
      params.add(minPrice);
    }
    if (maxPrice != null) {
      sql.append(" AND price <= ?");
      params.add(maxPrice);
    }
    switch (orderBy) {
      case "price" -> sql.append(" ORDER BY price ASC");
      case "popularity" -> sql.append(" ORDER BY popularity DESC");
      case "new" -> sql.append(" ORDER BY created_at DESC");
    }

    if (pageSize != null && page != null) {
      sql.append(" LIMIT ? OFFSET ?");
      params.add(pageSize);
      params.add(page * pageSize);
    }
    return jdbcTemplate.query(sql.toString(), productRowMapper, params.toArray());
  }

  @Override
  public boolean isProductExist(String title) {
    String sql = queryProvider.getSqlQueryForProduct("count_by_title");
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, title);
    return count != 0;
  }
}
