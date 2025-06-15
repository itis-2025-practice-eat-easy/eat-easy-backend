package com.technokratos.eateasy.cart.repository.impl;

import com.technokratos.eateasy.cart.entity.Cart;
import com.technokratos.eateasy.cart.repository.CartRepository;
import com.technokratos.eateasy.cart.util.QueryProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {

    private final JdbcTemplate jdbcTemplate;
    private final QueryProvider queryProvider;

    @Override
    public Cart save(Cart cart) {
        String sqlForUserCart = queryProvider.getSqlQueryForCart("save_user_cart");
        String sqlForCartProduct = queryProvider.getSqlQueryForCart("save_cart_product");

        try {
            UUID cartId = jdbcTemplate.query(
                    con -> {
                        PreparedStatement ps = con.prepareStatement(sqlForUserCart);
                        ps.setObject(1, cart.getUserId());
                        return ps;
                    },
                    rs -> {
                        if (rs.next()) return (UUID) rs.getObject("cart_id");
                        else throw new RuntimeException("Failed to retrieve cart_id after insert");
                    }
            );

            for (Map.Entry<UUID, Integer> entry : cart.getProducts().entrySet()) {
                jdbcTemplate.update(sqlForCartProduct, cartId, entry.getKey(), entry.getValue());
            }

            return findById(cartId);

        } catch (DataIntegrityViolationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Database error during cart save", e);
        }
    }

    @Override
    public Cart findById(UUID id) {
        String sqlCart = queryProvider.getSqlQueryForCart("find_cart_by_id");
        String sqlProducts = queryProvider.getSqlQueryForCart("find_products_by_cart_id");

        try {
            UUID userId = jdbcTemplate.queryForObject(sqlCart, new Object[]{id}, UUID.class);
            List<Map.Entry<UUID, Integer>> productEntries = jdbcTemplate.query(
                    sqlProducts,
                    new Object[]{id},
                    (rs, rowNum) -> Map.entry(
                            (UUID) rs.getObject("product_id"),
                            rs.getInt("quantity")
                    )
            );

            Map<UUID, Integer> products = productEntries.stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            return new Cart(id, userId, products);

        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Cart not found with id: " + id);
        }
    }

    @Override
    public List<Cart> findAll(UUID userId) {
        String sqlCarts = queryProvider.getSqlQueryForCart("find_carts_by_user");

        List<UUID> cartIds = jdbcTemplate.query(
                sqlCarts,
                new Object[]{userId},
                (rs, rowNum) -> (UUID) rs.getObject("cart_id")
        );

        return cartIds.stream()
                .map(this::findById)
                .toList();
    }

    @Override
    public void addToCart(UUID productId, Integer quantity, UUID userId) {
        String sqlFindCartId = queryProvider.getSqlQueryForCart("find_latest_cart_by_user");
        String sqlInsert = queryProvider.getSqlQueryForCart("save_cart_product");

        try {
            UUID cartId = jdbcTemplate.queryForObject(sqlFindCartId, new Object[]{userId}, UUID.class);
            jdbcTemplate.update(sqlInsert, cartId, productId, quantity);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("No cart found for user: " + userId);
        }
    }
}

