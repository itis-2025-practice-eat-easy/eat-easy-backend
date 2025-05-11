package com.technokratos.eateasy.product.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Schema(description = "Unique category identifier", example = "c7e2f6b4-98b8-4f98-89b2-8295e8d25b5a")
    private UUID id;

    @NotBlank(message = "Category title cannot be empty")
    @Size(max = 255, message = "Category title is longer than 255 characters")
    @Schema(description = "Category title", example = "Beverages")
    private String title;
}
