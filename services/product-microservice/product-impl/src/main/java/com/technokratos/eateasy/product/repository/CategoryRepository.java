package com.technokratos.eateasy.product.repository;


import com.technokratos.eateasy.product.entity.Category;
import com.technokratos.eateasy.product.entity.Product;
import com.technokratos.eateasy.product.util.QueryProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CategoryRepository {

    private final DataSource dataSource;
    private final QueryProvider queryProvider;


    public void assignCategoriesToProduct(List<UUID> categoriesId, UUID productId) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(queryProvider.getSqlQueryForCategory("assign_categories_to_product"))) {
                for (UUID category : categoriesId) {
                    statement.setObject(1, productId);
                    statement.setObject(2, category);
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        }

        catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }


    public Optional<List<Category>>getCategoriesByProductId(UUID productId) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(queryProvider.getSqlQueryForCategory("get_categories_by_product_id"))) {
                statement.setObject(1, productId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<Category> categories = new ArrayList<>();
                    while (resultSet.next()) {
                        categories.add(
                                Category.builder()
                                        .id(UUID.fromString(resultSet.getString("id")))
                                        .title(resultSet.getString("title"))
                                        .build()
                        );
                    }
                    return Optional.of(categories);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }


    public Category save(Category category) {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection
                        .prepareStatement(queryProvider.getSqlQueryForCategory("save"))) {
                    statement.setString(1, category.getTitle());
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        return Category.builder()
                                .id(UUID.fromString(resultSet.getString("id")))
                                .title(resultSet.getString("title"))
                                .build();
                    } else {
                        return null;
                    }
                }
            } catch (SQLException e) {
                String sqlState = e.getSQLState();
                if (sqlState != null && (
                        sqlState.startsWith("23") ||
                                sqlState.equals("22001") ||
                                sqlState.equals("22003")
                )) {
                    throw new DataIntegrityViolationException("Data integrity violation error", e);
                }
                throw new RuntimeException("Database error", e);
            }
    }

    public List<Category> findAll() {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(queryProvider.getSqlQueryForCategory("find_all"))) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<Category> categories = new ArrayList<>();
                        while (resultSet.next()) {
                            categories.add(
                                    Category.builder()
                                            .id(UUID.fromString(resultSet.getString("id")))
                                            .title(resultSet.getString("title"))
                                            .build()
                            );
                        }
                        return categories;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
}
