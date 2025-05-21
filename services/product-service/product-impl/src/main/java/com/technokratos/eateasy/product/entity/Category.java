package com.technokratos.eateasy.product.entity;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    private UUID id;

    @NotBlank(message = "Category title cannot be empty")
    @Size(max = 255, message = "Category title is longer than 255 characters")
    private String title;
}
