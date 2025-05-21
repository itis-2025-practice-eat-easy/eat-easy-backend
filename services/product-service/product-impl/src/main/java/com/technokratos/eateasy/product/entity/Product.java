package com.technokratos.eateasy.product.entity;

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
public class Product {

    private UUID id;

    @NotBlank(message = "Product title cannot be empty")
    @Size(max = 255, message = "Product title is longer than 255 characters")
    private String title;

    @NotBlank(message = "Product description cannot be empty")
    @Size(max = 1000, message = "Product description is longer than 1000 characters")
    private String description;

    @NotBlank(message = "Product photoUrl cannot be empty")
    @Size(max = 2048, message = "Product photoUrl is longer than 2048 characters")
    @Pattern(
            regexp = "^(https?|ftp)://[^\\s/$.?#].\\S*$",
            message = "Product photoUrl is not valid"
    )
    private String photoUrl;

    @NotNull(message = "Product price cannot be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must be a valid decimal number with up to two decimal places")
    private BigDecimal price;

    @NotNull(message = "Product quantity cannot be null")
    @Min(value = 0, message = "Product quantity cannot be negative")
    private Integer quantity;

    @NotNull(message = "Product creation timestamp cannot be null")
    private Timestamp createdAt;

    @NotNull(message = "Product popularity cannot be null")
    private Integer popularity;
}
