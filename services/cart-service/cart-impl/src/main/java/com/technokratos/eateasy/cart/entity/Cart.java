package com.technokratos.eateasy.cart.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;
@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Cart {
    private UUID id;
    private UUID userId;
    private Map<UUID, Integer> products;
}

