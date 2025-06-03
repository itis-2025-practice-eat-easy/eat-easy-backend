package com.technokratos.eateasy.product.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.technokratos.eateasy.product.config.TestRestTemplateConfig;
import com.technokratos.eateasy.product.dto.category.CategoryRequest;
import com.technokratos.eateasy.product.dto.category.CategoryResponse;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = TestRestTemplateConfig.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CategoryControllerIntegrationTest {

  @Autowired TestRestTemplate restTemplate;

  @Autowired NamedParameterJdbcTemplate jdbcTemplate;

  final String CATEGORY_NAME = "Fruits";

  @BeforeEach
  void clearDatabase() {
    jdbcTemplate.update("DELETE FROM product_category", new MapSqlParameterSource());
    jdbcTemplate.update("DELETE FROM product", new MapSqlParameterSource());
    jdbcTemplate.update("DELETE FROM category", new MapSqlParameterSource());
  }

  @Test
  void createCategoryTest() {
    CategoryRequest request = new CategoryRequest(CATEGORY_NAME);

    ResponseEntity<CategoryResponse> response =
        restTemplate.exchange(
            "/api/v1/categories",
            HttpMethod.POST,
            new HttpEntity<>(request),
            CategoryResponse.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    CategoryResponse category = response.getBody();
    assertNotNull(category);
    assertEquals(CATEGORY_NAME, category.title());

    jdbcTemplate.query(
        "SELECT * FROM category WHERE id = :id",
        new MapSqlParameterSource("id", category.id()),
        (RowCallbackHandler) rs -> assertEquals(CATEGORY_NAME, rs.getString("title")));
  }

  @Test
  void getAllCategoriesEmptyTest() {
    ResponseEntity<List<CategoryResponse>> response =
        restTemplate.exchange(
            "/api/v1/categories", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isEmpty());
  }

  @Test
  void getAllCategoriesTest() {
    restTemplate.postForEntity(
        "/api/v1/categories", new CategoryRequest(CATEGORY_NAME), CategoryResponse.class);

    ResponseEntity<List<CategoryResponse>> response =
        restTemplate.exchange(
            "/api/v1/categories", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    assertEquals(1, response.getBody().size());
    assertEquals(CATEGORY_NAME, response.getBody().get(0).title());
  }

  @Test
  void getProductsByCategoryEmptyTest() {
    UUID categoryId =
        restTemplate
            .postForEntity(
                "/api/v1/categories", new CategoryRequest(CATEGORY_NAME), CategoryResponse.class)
            .getBody()
            .id();

    ResponseEntity<List<ProductResponse>> response =
        restTemplate.exchange(
            "/api/v1/categories/" + categoryId + "/products",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {});

    assertTrue(response.getBody().isEmpty());
  }

  @Test
  void getProductsByCategoryWithFiltersTest() {
    UUID categoryId =
        restTemplate
            .postForEntity(
                "/api/v1/categories", new CategoryRequest(CATEGORY_NAME), CategoryResponse.class)
            .getBody()
            .id();

    String url =
        String.format(
            "/api/v1/categories/%s/products?order_by=price&page=0&page_size=5", categoryId);

    ResponseEntity<List<ProductResponse>> response =
        restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
