package com.technokratos.eateasy.product.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.eateasy.product.config.TestRestTemplateConfig;
import com.technokratos.eateasy.product.dto.category.CategoryRequest;
import com.technokratos.eateasy.product.dto.category.CategoryResponse;
import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(
        classes = TestRestTemplateConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

    @Autowired TestRestTemplate restTemplate;
    @Autowired NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired ObjectMapper objectMapper;

    final String CATEGORY_NAME = "Fruits";
    final ProductRequest BASE_PRODUCT = ProductRequest.builder()
            .title("Apple")
            .description("Fresh red apples")
            .price(BigDecimal.valueOf(2.99))
            .categories(List.of())
            .quantity(100)
            .build();

    UUID categoryId;
    ProductRequest testProduct;

    @BeforeEach
    void setup() {
        clearDatabase();
        categoryId = createCategory();
        testProduct = ProductRequest.builder()
                .title(BASE_PRODUCT.getTitle())
                .description(BASE_PRODUCT.getDescription())
                .price(BASE_PRODUCT.getPrice())
                .categories(List.of(categoryId))
                .quantity(BASE_PRODUCT.getQuantity())
                .build();
    }

    void clearDatabase() {
        jdbcTemplate.update("DELETE FROM product_category", new MapSqlParameterSource());
        jdbcTemplate.update("DELETE FROM product", new MapSqlParameterSource());
        jdbcTemplate.update("DELETE FROM category", new MapSqlParameterSource());
    }

    UUID createCategory() {
        CategoryRequest categoryRequest = new CategoryRequest(CATEGORY_NAME);
        ResponseEntity<CategoryResponse> response =
                restTemplate.exchange(
                        "/api/v1/categories",
                        HttpMethod.POST,
                        new HttpEntity<>(categoryRequest),
                        CategoryResponse.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        return response.getBody().id();
    }

    @Test
    void createProductTest() throws Exception {
        // JSON из ProductRequest в строку
        String productJson = objectMapper.writeValueAsString(testProduct);

        // Создаем заголовки и тело multipart/form-data
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("product", new ByteArrayResource(productJson.getBytes(StandardCharsets.UTF_8)) {
            @Override public String getFilename() {
                return "product.json"; // нужно для правильной обработки в контроллере
            }
        });

        // Для теста, добавим пустой файл картинки (или можешь заменить на реально байты картинки)
        byte[] dummyImage = new byte[]{1, 2, 3};
        Resource imageResource = new ByteArrayResource(dummyImage) {
            @Override public String getFilename() {
                return "avatar.png";
            }
        };
        body.add("avatarFile", imageResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<ProductResponse> response =
                restTemplate.exchange("/api/v1/products", HttpMethod.POST, requestEntity, ProductResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ProductResponse createdProduct = response.getBody();
        assertNotNull(createdProduct);
        assertEquals(testProduct.getTitle(), createdProduct.title());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM product_category WHERE product_id = :productId AND category_id = :categoryId",
                new MapSqlParameterSource()
                        .addValue("productId", createdProduct.id())
                        .addValue("categoryId", categoryId),
                Integer.class);

        assertEquals(1, count);
    }

    @Test
    void getProductByIdTest() {
        UUID productId = createTestProduct().id();

        ResponseEntity<ProductResponse> response =
                restTemplate.exchange(
                        "/api/v1/products/" + productId, HttpMethod.GET, null, ProductResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productId, response.getBody().id());
    }

    @Test
    void updateProductQuantityTest() {
        UUID productId = createTestProduct().id();
        Integer addValue = 50;
        Integer oldQuantity =
                jdbcTemplate.queryForObject(
                        "SELECT quantity FROM product WHERE id = :id",
                        new MapSqlParameterSource("id", productId),
                        Integer.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Integer> requestEntity = new HttpEntity<>(addValue, headers);

        ResponseEntity<Void> response =
                restTemplate.exchange(
                        "/api/v1/products/" + productId + "/count",
                        HttpMethod.PATCH,
                        requestEntity,
                        Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        Integer updatedQuantity =
                jdbcTemplate.queryForObject(
                        "SELECT quantity FROM product WHERE id = :id",
                        new MapSqlParameterSource("id", productId),
                        Integer.class);

        assertEquals(oldQuantity + addValue, updatedQuantity);
    }

    @Test
    void updateProductTest() throws Exception {
        UUID productId = createTestProduct().id();

        ProductRequest updateRequest = ProductRequest.builder()
                .title("Green Apple")
                .price(BigDecimal.valueOf(3.99))
                .build();

        String updateJson = objectMapper.writeValueAsString(updateRequest);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("product", new ByteArrayResource(updateJson.getBytes(StandardCharsets.UTF_8)) {
            @Override public String getFilename() {
                return "product-update.json";
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Void> response =
                restTemplate.exchange(
                        "/api/v1/products/" + productId,
                        HttpMethod.PATCH,
                        requestEntity,
                        Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        String updatedTitle =
                jdbcTemplate.queryForObject(
                        "SELECT title FROM product WHERE id = :id",
                        new MapSqlParameterSource("id", productId),
                        String.class);

        assertEquals("Green Apple", updatedTitle);
    }

    @Test
    void deleteProductTest() {
        UUID productId = createTestProduct().id();

        ResponseEntity<Void> response =
                restTemplate.exchange(
                        "/api/v1/products/" + productId,
                        HttpMethod.DELETE,
                        null,
                        Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        Integer count =
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM product WHERE id = :id",
                        new MapSqlParameterSource("id", productId),
                        Integer.class);

        assertEquals(0, count);
    }

    private ProductResponse createTestProduct() {
        try {
            String productJson = objectMapper.writeValueAsString(testProduct);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("product", new ByteArrayResource(productJson.getBytes(StandardCharsets.UTF_8)) {
                @Override public String getFilename() {
                    return "product.json";
                }
            });

            byte[] dummyImage = new byte[]{1, 2, 3};
            Resource imageResource = new ByteArrayResource(dummyImage) {
                @Override public String getFilename() {
                    return "avatar.png";
                }
            };
            body.add("avatarFile", imageResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<ProductResponse> response =
                    restTemplate.exchange("/api/v1/products", HttpMethod.POST, requestEntity, ProductResponse.class);

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
