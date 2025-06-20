package com.technokratos.eateasy.product.repository.impl;

import com.technokratos.eateasy.product.entity.Category;
import com.technokratos.eateasy.product.exception.CategoryDataIntegrityViolationException;
import com.technokratos.eateasy.product.exception.CategoryDatabaseException;
import com.technokratos.eateasy.product.repository.CategoryRepository;
import com.technokratos.eateasy.product.util.QueryProvider;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

  private final JdbcTemplate jdbcTemplate;
  private final QueryProvider queryProvider;

  private final RowMapper<Category> categoryRowMapper =
      (rs, rowNum) ->
          Category.builder()
              .id(UUID.fromString(rs.getString("id")))
              .title(rs.getString("title"))
              .build();

  public void assignCategoriesToProduct(List<UUID> categoriesId, UUID productId) {
    String sql = queryProvider.getSqlQueryForCategory("assign_categories_to_product");
    List<Object[]> batchArgs =
        categoriesId.stream().map(categoryId -> new Object[] {productId, categoryId}).toList();
    jdbcTemplate.batchUpdate(sql, batchArgs);
  }

  public List<Category> getCategoriesByProductId(UUID productId) {
    String sql = queryProvider.getSqlQueryForCategory("get_categories_by_product_id");
    return jdbcTemplate.query(sql, categoryRowMapper, productId);
  }

  public Category save(Category category) {
    String sql = queryProvider.getSqlQueryForCategory("save");
    try {
      return jdbcTemplate.queryForObject(sql, categoryRowMapper, category.getTitle());
    } catch (DataIntegrityViolationException e) {
      throw new CategoryDataIntegrityViolationException(e.getMessage());
    } catch (Exception e) {
      throw new CategoryDatabaseException(e.getMessage());
    }
  }

  public List<Category> findAll() {
    String sql = queryProvider.getSqlQueryForCategory("find_all");
    return jdbcTemplate.query(sql, categoryRowMapper);
  }

  public Optional<Category> findById(UUID id) {
    return jdbcTemplate
        .query(queryProvider.getSqlQueryForCategory("find_by_id"), categoryRowMapper, id)
        .stream()
        .findFirst();
  }

  public void updateCategoriesByProductId(List<UUID> categories, UUID productId) {
    jdbcTemplate.update(
        queryProvider.getSqlQueryForCategory("delete_from_summary_table"), productId);
    assignCategoriesToProduct(categories, productId);
  }
}
