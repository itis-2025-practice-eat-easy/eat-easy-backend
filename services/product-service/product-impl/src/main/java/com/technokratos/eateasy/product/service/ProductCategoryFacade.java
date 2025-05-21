package com.technokratos.eateasy.product.service;

import com.technokratos.eateasy.product.dto.product.ProductRequest;
import com.technokratos.eateasy.product.dto.product.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
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

    public List<ProductResponse> getProductsByCategoryId(UUID id,
                                                         String order_by,
                                                         Integer page,
                                                         Integer page_size,
                                                         BigDecimal max_price,
                                                         BigDecimal min_price) {
        categoryService.getById(id); //check if category exists
        List<ProductResponse> products =  productService
                .getByCategoryId(id, order_by, page, page_size, max_price, min_price);
        products.forEach(product -> {
            product.categories().addAll(categoryService.getCategoriesByProductId(product.id()));
        });
        return products;
    }
}
