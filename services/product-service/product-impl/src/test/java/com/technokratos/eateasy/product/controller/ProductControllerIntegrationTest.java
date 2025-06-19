package com.technokratos.eateasy.product.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.technokratos.eateasy.product.config.TestRestTemplateConfig;
import com.technokratos.eateasy.product.dto.category.CategoryRequest;
import com.technokratos.eateasy.product.dto.category.CategoryResponse;
import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = TestRestTemplateConfig.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

  @Autowired TestRestTemplate restTemplate;

  @Autowired NamedParameterJdbcTemplate jdbcTemplate;

  final String CATEGORY_NAME = "Fruits";
  final ProductRequest BASE_PRODUCT =
      new ProductRequest(
          "Apple",
          "Fresh red apples",
          "http://example.com/apple.jpg",
          BigDecimal.valueOf(2.99),
          List.of(),
          100);

  UUID categoryId;
  ProductRequest testProduct;

  @BeforeEach
  void setup() {
    clearDatabase();
    categoryId = createCategory();
    testProduct =
        new ProductRequest(
            BASE_PRODUCT.title(),
            BASE_PRODUCT.description(),
            BASE_PRODUCT.photoUrl(),
            BASE_PRODUCT.price(),
            List.of(categoryId),
            BASE_PRODUCT.quantity());
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
    return response.getBody().id();
  }

  @Test
  void createProductTest() {
    ResponseEntity<ProductResponse> response =
        restTemplate.exchange(
            "/api/v1/products",
            HttpMethod.POST,
            new HttpEntity<>(testProduct),
            ProductResponse.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    ProductResponse createdProduct = response.getBody();

    assertNotNull(createdProduct);
    assertEquals(testProduct.title(), createdProduct.title());

    Integer count =
        jdbcTemplate.queryForObject(
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

    ResponseEntity<Void> response =
        restTemplate.exchange(
            "/api/v1/products/" + productId + "/count",
            HttpMethod.PATCH,
            new HttpEntity<>(addValue),
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
  void updateProductTest() {
    UUID productId = createTestProduct().id();
    ProductRequest updateRequest =
        ProductRequest.builder().title("Green Apple").price(BigDecimal.valueOf(3.99)).build();

    ResponseEntity<Void> response =
        restTemplate.exchange(
            "/api/v1/products/" + productId,
            HttpMethod.PATCH,
            new HttpEntity<>(updateRequest),
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
        restTemplate.exchange("/api/v1/products/" + productId, HttpMethod.DELETE, null, Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    Integer count =
        jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM product WHERE id = :id",
            new MapSqlParameterSource("id", productId),
            Integer.class);
    assertEquals(0, count);
  }

  private ProductResponse createTestProduct() {
    return restTemplate
        .postForEntity("/api/v1/products", testProduct, ProductResponse.class)
        .getBody();
  }
}
