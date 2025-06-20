package com.technokratos.eateasy.product.dto.product;

import com.technokratos.eateasy.product.validation.AtLeastOneFieldNotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request for updating an existing product (partial update)")
@AtLeastOneFieldNotNull(message = "Product update data cannot be null")
public class ProductUpdateRequest {

        @Size(max = 255, message = "Product title is longer than 255 characters")
        @Schema(description = "Product name", example = "Gala Apple")
        private String title;

        @Size(max = 1000, message = "Product description is longer than 1000 characters")
        @Schema(description = "Detailed product description", example = "Fresh and juicy Gala apples, grown locally in California.")
        private String description;

        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Price must be a valid decimal number with up to two decimal places")
        @Schema(description = "Product price in USD", example = "1.99")
        private BigDecimal price;

        @Schema(description = "List of category UUIDs", example = "[\"c7e2f6b4-98b8-4f98-89b2-8295e8d25b5a\"]")
        private List<UUID> categories;

        @Min(value = 0, message = "Product quantity cannot be negative")
        @Schema(description = "Available quantity in stock", example = "200")
        private Integer quantity;

        @Schema(description = "Product photo UUID from 'image-reference' table", example = "d7e2f6b4-98b8-4f98-89b2-8295e8d25b5a")
        private UUID photoUrlId;
}
