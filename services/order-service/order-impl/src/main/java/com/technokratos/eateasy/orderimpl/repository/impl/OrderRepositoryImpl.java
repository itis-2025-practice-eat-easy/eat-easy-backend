package com.technokratos.eateasy.orderimpl.repository.impl;

import com.technokratos.eateasy.orderapi.StatusResponseDto;
import com.technokratos.eateasy.orderimpl.model.OrderEntity;
import com.technokratos.eateasy.orderimpl.repository.OrderRepository;
import lombok.AllArgsConstructor;
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
            INSERT INTO orders (id,cart_id, user_id, delivery_address)
            VALUES (?, ?, ?, ?)
            """;

    private static final String GET_ALL_ACTUAL_SQL = """
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
            WHERE latest_status.getLayer() > 1;       
            """;

    private static final String GET_ALL_SQL = """
            SELECT * FROM orders
            ORDER BY id
            LIMIT ? OFFSET ? 
            """;

    @Override
    public Optional<OrderEntity> findById(UUID id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        return Optional.of(jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(OrderEntity.class), id));
    }

    @Override
    public List<StatusResponseDto> getListOfAllStatus(UUID orderId) {
        return null;
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
    public List<OrderEntity> findAll(Pageable pageable) {
        return jdbcTemplate.query(GET_ALL_SQL,
                new BeanPropertyRowMapper<>(OrderEntity.class),
                pageable.getPageSize(),
                pageable.getOffset());
    }

    @Override
    public List<OrderEntity> findAllActual(Pageable pageable) {
        return jdbcTemplate.query(GET_ALL_ACTUAL_SQL,
                new BeanPropertyRowMapper<>(OrderEntity.class),
                pageable.getPageSize(),
                pageable.getOffset());
    }

}
