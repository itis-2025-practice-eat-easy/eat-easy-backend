package com.technokratos.eateasy.product.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Request object for category data")
public record CategoryRequest(

        @NotBlank(message = "Category title cannot be empty")
        @Size(max = 255, message = "Category title is longer than 255 characters")
        @Schema(description = "Category title", example = "Beverages")
        String title
) {}
