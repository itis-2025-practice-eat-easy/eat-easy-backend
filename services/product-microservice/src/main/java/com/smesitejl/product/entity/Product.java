package com.smesitejl.product.entity;
import com.smesitejl.product.validation.ValidProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Schema(description = "Grocery product or food item")
@ValidProduct
public class Product {

    @Schema(description = "Unique product identifier", example = "c7e2f6b4-98b8-4f98-89b2-8295e8d25b5a")
    private UUID id;

    @Schema(description = "Product name", example = "Gala Apple")
    private String title;

    @Schema(description = "Detailed product description", example = "Fresh and juicy Gala apples, grown locally in California.")
    private String description;

    @Schema(description = "URL to the product image", example = "http://example.com/images/apple.jpg")
    private String photoUrl;

    @Schema(description = "Product price in USD", example = "1.99")
    private BigDecimal price;

    @Schema(description = "Product category", example = "Fruits")
    private String category;

    @Schema(description = "Available quantity in stock (e.g., units or packages)", example = "200")
    private Integer quantity;
}
