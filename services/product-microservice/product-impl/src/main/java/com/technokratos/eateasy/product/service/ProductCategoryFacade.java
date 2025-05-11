package com.technokratos.eateasy.product.service;

import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class ProductCategoryFacade {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductCategoryFacade(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    public ProductResponse getById(UUID id){
        ProductResponse response = productService.getById(id);
        response.categories().addAll(categoryService
                .getCategoriesByProductId(id));
        return response;
    }
    @Transactional
    public ProductResponse create(ProductRequest productRequest) {
        ProductResponse response = productService.create(productRequest);
        categoryService.assignCategoriesToProduct(productRequest.categories(), response.id());
        response.categories().addAll(categoryService
                .getCategoriesByProductId(response.id()));
        log.info("Created product with id: {}", response.id());
        return response;
    }

    @Transactional
    public void update(UUID id, ProductRequest productRequest) {
        if (!(productRequest.categories() == null || productRequest.categories().isEmpty())){
            categoryService.updateCategoriesByProductId(productRequest.categories(), id);

        }
        log.info("Updated product with id: {}", id);
        productService.update(id, productRequest);
    }

}
