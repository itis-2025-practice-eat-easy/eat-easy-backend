package com.technokratos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ProductRequest(

        @NotNull(message = "Product ID must not be null")
        UUID id,

        @NotNull(message = "Quantity must not be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity

) {}