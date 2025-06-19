package com.technokratos.eateasy.orderimpl.controller;

import com.technokratos.eateasy.orderapi.dto.OrderRequestDto;
import com.technokratos.eateasy.orderapi.dto.OrderResponseDto;
import com.technokratos.eateasy.orderimpl.config.TestRestTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestRestTemplateConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ActiveProfiles(profiles = "test")
public class OrderControllerIntegrationTest {
    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;
    private UUID createdOrderId;
    private UUID userId;
    private final String DELIVERY_ADDRESS = "Test Address, 123";
    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.getJdbcTemplate().execute("DELETE FROM orders");
    }
    @Test
    void createOrderTest() {
        userId = UUID.randomUUID();
        OrderRequestDto requestDto = new OrderRequestDto(userId, DELIVERY_ADDRESS);
        ResponseEntity<OrderResponseDto> response = testRestTemplate.exchange(
                "/api/v1/orders",
                HttpMethod.POST,
                new HttpEntity<>(requestDto),
                OrderResponseDto.class
        );
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        OrderResponseDto responseBody = response.getBody();
        assertNotNull(responseBody);
        createdOrderId = responseBody.cartId();
        assertEquals(userId, responseBody.userId());
        assertEquals(DELIVERY_ADDRESS, responseBody.deliveryAddress());
        jdbcTemplate.query(
                "SELECT * FROM orders WHERE cart_id = :cartId",
                new MapSqlParameterSource("cartId", createdOrderId),
                rs -> {
                    assertEquals(userId.toString(), rs.getString("user_id"));
                    assertEquals(DELIVERY_ADDRESS, rs.getString("delivery_address"));
                    assertFalse(rs.next());
                }
        );
    }

    @Test
    void getOrderByIdNotFoundTest() {
        UUID nonExistentId = UUID.randomUUID();
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/orders/" + nonExistentId,
                HttpMethod.GET,
                null,
                String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getOrderStatusHistoryNotFoundTest() {
        UUID nonExistentId = UUID.randomUUID();
        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/orders/" + nonExistentId + "/info",
                HttpMethod.GET,
                null,
                String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
