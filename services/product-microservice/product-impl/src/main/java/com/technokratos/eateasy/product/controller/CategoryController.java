package com.technokratos.eateasy.product.controller;

import com.technokratos.eateasy.product.api.CategoryApi;
import com.technokratos.eateasy.product.dto.category.CategoryRequest;
import com.technokratos.eateasy.product.dto.category.CategoryResponse;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import com.technokratos.eateasy.product.entity.Category;
import com.technokratos.eateasy.product.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CategoryController implements CategoryApi {

    private final CategoryService categoryService;

    @Override
    public ResponseEntity<List<CategoryResponse>> getAll() {
        log.info("Received request to get all categories");
        return ResponseEntity.ok(categoryService.getAll());
    }

    @Override
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest category) {
        log.info("Received request to create a category {}", category);
        CategoryResponse savedCategory = categoryService.create(category);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCategory.id())
                .toUri();
        return ResponseEntity.created(location).body(savedCategory);
    }

    @Override
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(Long id,
                                                                       String order_by,
                                                                       String page,
                                                                       String page_size,
                                                                       String max_price,
                                                                       String min_price) {
        return categoryService.getProductsByCategory();
    }
}
