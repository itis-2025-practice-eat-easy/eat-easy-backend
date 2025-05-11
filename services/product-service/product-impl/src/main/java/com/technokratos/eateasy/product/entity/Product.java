package com.technokratos.eateasy.product.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Grocery product or food item")
public class Product {

    @Schema(description = "Unique product identifier", example = "c7e2f6b4-98b8-4f98-89b2-8295e8d25b5a")
    private UUID id;

    @NotBlank(message = "Product title cannot be empty")
    @Size(max = 255, message = "Product title is longer than 255 characters")
    @Schema(description = "Product name", example = "Gala Apple")
    private String title;

    @NotBlank(message = "Product description cannot be empty")
    @Size(max = 1000, message = "Product description is longer than 1000 characters")
    @Schema(description = "Detailed product description", example = "Fresh and juicy Gala apples, grown locally in California.")
    private String description;

    @NotBlank(message = "Product photoUrl cannot be empty")
    @Size(max = 2048, message = "Product photoUrl is longer than 2048 characters")
    @Pattern(
            regexp = "^(https?|ftp)://[^\\s/$.?#].\\S*$",
            message = "Product photoUrl is not valid"
    )
    @Schema(description = "URL to the product image", example = "http://example.com/images/apple.jpg")
    private String photoUrl;

    @NotNull(message = "Product price cannot be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must be a valid decimal number with up to two decimal places")
    @Schema(description = "Product price in USD", example = "1.99")
    private BigDecimal price;

    @NotNull(message = "Product quantity cannot be null")
    @Min(value = 0, message = "Product quantity cannot be negative")
    @Schema(description = "Available quantity in stock (e.g., units or packages)", example = "200")
    private Integer quantity;

    @Schema(description = "Timestamp when the product was created", example = "2025-05-10T15:30:00")
    @NotNull(message = "Product creation timestamp cannot be null")
    private Timestamp createdAt;

    @Schema(description = "Popularity of the product (how many times it was purchased)", example = "125")
    @NotNull(message = "Product popularity cannot be null")
    private Integer popularity;
}
