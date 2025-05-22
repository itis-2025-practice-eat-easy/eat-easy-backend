package com.technokratos.eateasy.product.dto.product;

import com.technokratos.eateasy.product.validation.AtLeastOneFieldNotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@AtLeastOneFieldNotNull(message = "Product update data cannot be null")
public record ProductUpdateRequest(
        @Size(max = 255, message = "Product title is longer than 255 characters")
        @Schema(description = "Product name", example = "Gala Apple")
        String title,

        @Size(max = 1000, message = "Product description is longer than 1000 characters")
        @Schema(description = "Detailed product description", example = "Fresh and juicy Gala apples, grown locally in California.")
        String description,

        @Size(max = 2048, message = "Product photoUrl is longer than 2048 characters")
        @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].\\S*$", message = "Product photoUrl is not valid")
        @Schema(description = "URL to the product image", example = "http://example.com/images/apple.jpg")
        String photoUrl,

        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Price must be a valid decimal number with up to two decimal places")
        @Schema(description = "Product price in USD", example = "1.99")
        BigDecimal price,

        @Size(max = 255, message = "Product category is longer than 255 characters")
        @Schema(description = "Product categories", example = "c7e2f6b4-98b8-4f98-89b2-8295e8d25b5a")
        List<UUID> categories,

        @Min(value = 0, message = "Product quantity cannot be negative")
        @Schema(description = "Available quantity in stock (e.g., units or packages)", example = "200")
        Integer quantity
) {}

