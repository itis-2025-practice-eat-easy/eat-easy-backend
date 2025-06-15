package com.technokratos.eateasy.cart.repository.impl;

import com.technokratos.eateasy.cart.entity.Cart;
import com.technokratos.eateasy.cart.exception.CartNotFoundException;
import com.technokratos.eateasy.cart.repository.CartRepository;
import com.technokratos.eateasy.cart.util.QueryProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {

    private final JdbcTemplate jdbcTemplate;
    private final QueryProvider queryProvider;

    private final RowMapper<Cart> cartRowMapper = (rs, rowNum) -> Cart.builder()
            .id((UUID) rs.getObject("cart_id"))
            .userId((UUID) rs.getObject("user_id"))
            .isBlocked(rs.getBoolean("is_blocked"))
            .createdAt(rs.getTimestamp("created_at"))
            .products(new HashMap<>())
            .build();

    private void createActiveCartIfNotExists(UUID userId) {
        String checkSql = queryProvider.getSqlQueryForCart("check_active_cart_exists");

        Boolean exists = jdbcTemplate.query(
                checkSql,
                ps -> ps.setObject(1, userId),
                rs -> rs.next() && rs.getBoolean(1)
        );

        if (!Boolean.TRUE.equals(exists)) {
            save(Cart.builder().userId(userId).build());
        }
    }


    private void save(Cart cart) {
        String sqlForUserCart = queryProvider.getSqlQueryForCart("save_user_cart");

        try {
            int rowsAffected = jdbcTemplate.update(sqlForUserCart, ps -> {
                ps.setObject(1, cart.getUserId());
            });

            if (rowsAffected == 0) {
                throw new RuntimeException("Cart insert failed: no rows affected");
            }

        } catch (DataIntegrityViolationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Database error during cart save", e);
        }
    }

    @Override
    public Optional<Cart> findById(UUID id) {
        String sqlCart = queryProvider.getSqlQueryForCart("find_cart_by_id");
        String sqlProducts = queryProvider.getSqlQueryForCart("find_products_by_cart_id");

        List<Cart> carts = jdbcTemplate.query(sqlCart, new Object[]{id}, cartRowMapper);
        if (carts.isEmpty()) {
            return Optional.empty();
        }

        Cart cart = carts.get(0);

        List<Map.Entry<UUID, Integer>> productEntries = jdbcTemplate.query(
                sqlProducts,
                new Object[]{id},
                (rs, rowNum) -> Map.entry(
                        (UUID) rs.getObject("product_id"),
                        rs.getInt("quantity")
                )
        );

        Map<UUID, Integer> products = productEntries.stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (q1, q2) -> q1
                ));

        cart.setProducts(products);

        return Optional.of(cart);
    }



    @Override
    public List<Cart> findAll(UUID userId) {
        String sqlCarts = queryProvider.getSqlQueryForCart("find_carts_by_user");

        createActiveCartIfNotExists(userId);

        List<UUID> userCartsIds = jdbcTemplate.query(
                sqlCarts,
                new Object[]{userId},
                (rs, rowNum) -> (UUID) rs.getObject("cart_id")
        );

        return userCartsIds.stream()
                .map(this::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public void addToCart(UUID productId, Integer quantity, UUID cartId) {
        String checkBlockedSql = queryProvider.getSqlQueryForCart("check_if_cart_blocked");
        String sqlInsert = queryProvider.getSqlQueryForCart("save_cart_product");

        Optional<Cart> cart = findById(cartId);
        if(cart.isEmpty()) {
            throw new CartNotFoundException(cartId);
        }
        else if(cart.get().getIsBlocked() == true) {
            throw new RuntimeException("Cannot add to cart: cart is blocked");

        }

        try {
            jdbcTemplate.update(sqlInsert, cartId, productId, quantity);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("No cart found for cart: " + cartId);
        }
    }
}

