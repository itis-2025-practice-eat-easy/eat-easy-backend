package com.technokratos.eateasy.product.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;

@Schema(description = "Response object for category data")
@Builder
public record CategoryResponse(
    @Schema(
            description = "Unique category identifier",
            example = "c7e2f6b4-98b8-4f98-89b2-8295e8d25b5a")
        UUID id,
    @Schema(description = "Category title", example = "Beverages") String title) {}
