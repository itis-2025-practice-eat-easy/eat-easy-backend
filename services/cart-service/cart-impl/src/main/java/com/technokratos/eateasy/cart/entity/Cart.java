package com.technokratos.eateasy.cart.entity;

import lombok.*;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Cart {
    private UUID id;
    private UUID userId;
    private Boolean isBlocked;
    private Timestamp createdAt;
    private Map<UUID, Integer> products;
}

