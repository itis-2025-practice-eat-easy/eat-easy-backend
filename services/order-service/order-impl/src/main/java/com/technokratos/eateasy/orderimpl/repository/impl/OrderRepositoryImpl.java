package com.technokratos.eateasy.orderimpl.repository.impl;

import com.technokratos.eateasy.common.exception.NotFoundServiceException;
import com.technokratos.eateasy.orderimpl.model.OrderEntity;
import com.technokratos.eateasy.orderimpl.model.OrderLogEntity;
import com.technokratos.eateasy.orderimpl.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String SAVE_SQL = """
        INSERT INTO orders (id, cart_id, user_id, delivery_address)
        VALUES (?, ?, ?, ?)
    """;
    private static final String GET_ALL_STATUS_BY_ORDER_ID_SQL = """
        SELECT * FROM orders_log
        WHERE order_id = ?
        ORDER BY created_at
    """;
    private static final String COUNT_BY_USER_SQL = """
        SELECT COUNT(*) FROM orders WHERE user_id = ?
    """;
    private static final String CHECK_ORDER_EXISTS_SQL = """
        SELECT EXISTS (SELECT 1 FROM orders WHERE cart_id = ?)
    """;
    private static final String GET_ALL_BY_USER_SQL = """
        SELECT * FROM orders
        WHERE user_id = ?
        ORDER BY id
        LIMIT ? OFFSET ?
    """;
    private static final String GET_ALL_ACTUAL_BY_USER_SQL = """
        SELECT o.*
        FROM orders o
        JOIN (
            SELECT order_id, status
            FROM orders_log ol1
            WHERE created_at = (
                SELECT MAX(created_at)
                FROM orders_log ol2
                WHERE ol2.order_id = ol1.order_id
            )
        ) latest_status ON latest_status.order_id = o.id
        WHERE o.user_id = ?
        AND latest_status.status NOT IN ('DELIVERED')
        ORDER BY o.id
        LIMIT ? OFFSET ?
    """;
    @Override
    public Optional<OrderEntity> findById(UUID id) {
        try {
            String sql = "SELECT * FROM orders WHERE id = ?";
            return Optional.of(jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(OrderEntity.class), id));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundServiceException("Order not found with id: " + id);
        }
    }
    @Override
    public List<OrderLogEntity> getListOfAllStatus(UUID orderId) {
        return jdbcTemplate.query(
                GET_ALL_STATUS_BY_ORDER_ID_SQL,
                new BeanPropertyRowMapper<>(OrderLogEntity.class),
                orderId
        );
    }
    @Override
    public void save(OrderEntity order) {
        jdbcTemplate.update(SAVE_SQL,
                order.getId(),
                order.getCartId(),
                order.getUserId(),
                order.getDeliveryAddress()
        );
    }
    @Override
    public int countByUser(UUID userId) {
        return jdbcTemplate.queryForObject(COUNT_BY_USER_SQL, Integer.class, userId);
    }

    @Override
    public List<OrderEntity> findAllByUser(UUID userId, Pageable pageable) {
        return jdbcTemplate.query(GET_ALL_BY_USER_SQL,
                new BeanPropertyRowMapper<>(OrderEntity.class),
                userId,
                pageable.getPageSize(),
                pageable.getOffset());
    }

    @Override
    public List<OrderEntity> findAllActualByUser(UUID userId, Pageable pageable) {
        return jdbcTemplate.query(GET_ALL_ACTUAL_BY_USER_SQL,
                new BeanPropertyRowMapper<>(OrderEntity.class),
                userId,
                pageable.getPageSize(),
                pageable.getOffset());
    }
    @Override
    public boolean isOrderWithThisCartIdExist(UUID cartId) {
        return jdbcTemplate.queryForObject(CHECK_ORDER_EXISTS_SQL, Boolean.class, cartId);
    }
}
