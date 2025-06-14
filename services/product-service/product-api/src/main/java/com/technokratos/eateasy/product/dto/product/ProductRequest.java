package com.technokratos.eateasy.product.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ProductRequest(
    @NotBlank(message = "Product title cannot be empty")
        @Size(max = 255, message = "Product title is longer than 255 characters")
        @Schema(description = "Product name", example = "Gala Apple")
        String title,
    @NotBlank(message = "Product description cannot be empty")
        @Size(max = 1000, message = "Product description is longer than 1000 characters")
        @Schema(
            description = "Detailed product description",
            example = "Fresh and juicy Gala apples, grown locally in California.")
        String description,
    @NotBlank(message = "Product photoUrl cannot be empty")
        @Size(max = 2048, message = "Product photoUrl is longer than 2048 characters")
        @Pattern(
            regexp = "^(https?|ftp)://[^\\s/$.?#].\\S*$",
            message = "Product photoUrl is not valid")
        @Schema(
            description = "URL to the product image",
            example = "http://example.com/images/apple.jpg")
        String photoUrl,
    @NotNull(message = "Product price cannot be null")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @Digits(
            integer = 10,
            fraction = 2,
            message = "Price must be a valid decimal number with up to two decimal places")
        @Schema(description = "Product price in USD", example = "1.99")
        BigDecimal price,
    @NotEmpty(message = "Product category cannot be empty")
        @Size(min = 1, max = 100, message = "Must contain between 1 and 100 categories")
        @Schema(
            description = "Product categories",
            example = "c7e2f6b4-98b8-4f98-89b2-8295e8d25b5a")
        List<UUID> categories,
    @NotNull(message = "Product quantity cannot be null")
        @Min(value = 0, message = "Product quantity cannot be negative")
        @Schema(
            description = "Available quantity in stock (e.g., units or packages)",
            example = "200")
        Integer quantity) {}
