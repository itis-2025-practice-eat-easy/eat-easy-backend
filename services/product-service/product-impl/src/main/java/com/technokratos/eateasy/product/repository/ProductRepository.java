package com.technokratos.eateasy.product.repository;



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
import java.util.*;

@Repository
@RequiredArgsConstructor
public class ProductRepository {

    private final DataSource dataSource;
    private final QueryProvider queryProvider;


   public Optional<Product> findById(UUID productId) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(queryProvider.getSqlQueryForProduct("find_by_id"))) {
                statement.setObject(1, productId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(Product.builder()
                                .id(UUID.fromString(resultSet.getString("id")))
                                .title(resultSet.getString("title"))
                                .description(resultSet.getString("description"))
                                .photoUrl(resultSet.getString("photo_url"))
                                .price(resultSet.getBigDecimal("price"))
                                .quantity(resultSet.getInt("quantity"))
                                .createdAt(resultSet.getTimestamp("created_at"))
                                .popularity(resultSet.getInt("popularity"))
                                .build());
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public Product save(Product product) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(queryProvider.getSqlQueryForProduct("save"))) {
                statement.setString(1, product.getTitle());
                statement.setString(2, product.getDescription());
                statement.setString(3, product.getPhotoUrl());
                statement.setBigDecimal(4, product.getPrice());
                statement.setInt(5, product.getQuantity());
                statement.setTimestamp(6, product.getCreatedAt());
                statement.setInt(7,product.getPopularity());
                try(ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Product.builder()
                                .id(UUID.fromString(resultSet.getString("id")))
                                .title(resultSet.getString("title"))
                                .description(resultSet.getString("description"))
                                .photoUrl(resultSet.getString("photo_url"))
                                .price(resultSet.getBigDecimal("price"))
                                .quantity(resultSet.getInt("quantity"))
                                .createdAt(resultSet.getTimestamp("created_at"))
                                .popularity(resultSet.getInt("popularity"))
                                .build();
                    } else {
                        return null;
                    }
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

    public int updateQuantityIfNotNegative(UUID productId, Integer quantity) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(queryProvider.getSqlQueryForProduct("update_quantity"))) {
                statement.setObject(1, productId);
                statement.setInt(2, quantity);
                return statement.executeUpdate();
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

    public int update(UUID productId, Map<String, Object> updates) {
        if (updates.isEmpty()) return 0;

        Set<String> allowedColumns = Set.of("title", "description", "photo_url", "price", "category", "quantity");

        StringBuilder sql = new StringBuilder("UPDATE product SET ");
        List<Object> params = new ArrayList<>();

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String column = entry.getKey();
            if (!allowedColumns.contains(column)) {
                throw new IllegalArgumentException("Invalid column name: " + column);
            }
            sql.append(column).append(" = ?, ");
            params.add(entry.getValue());
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        params.add(productId);

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                return ps.executeUpdate();
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


    public int deleteById(UUID productId) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(queryProvider.getSqlQueryForProduct("delete_by_id"))) {
                statement.setObject(1, productId);
                return statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

}
