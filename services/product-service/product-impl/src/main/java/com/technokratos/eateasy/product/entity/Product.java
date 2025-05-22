package com.technokratos.eateasy.product.entity;

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
    private String title;
    private String description;
    private String photoUrl;
    private BigDecimal price;
    private Integer quantity;
    private Timestamp createdAt;
    private Integer popularity;
}
