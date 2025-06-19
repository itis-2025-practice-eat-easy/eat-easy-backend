package com.technokratos.eateasy.product.dto.product;

import com.technokratos.eateasy.product.dto.category.CategoryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

        @Schema(
                description = "Unique product identifier",
                example = "c7e2f6b4-98b8-4f98-89b2-8295e8d25b5a")
        private UUID id;

        @Schema(description = "Product name", example = "Gala Apple")
        private String title;

        @Schema(
                description = "Detailed product description",
                example = "Fresh and juicy Gala apples, grown locally in California.")
        private String description;

        @Schema(
                description = "Product photo URL from 'image-reference' table",
                example = "")
        private String photoUrl;

        @Schema(description = "Product price in USD", example = "1.99")
        private BigDecimal price;

        @Schema(description = "Product category", example = "Fruits")
        private List<CategoryResponse> categories;

        @Schema(description = "Available quantity in stock (e.g., units or packages)", example = "200")
        private Integer quantity;

        @Schema(description = "Product creation date", example = "2025-05-08T12:00:00")
        private LocalDateTime createdAt;

        @Schema(description = "Product popularity", example = "5")
        private Integer popularity;
}
