package com.technokratos.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record  CartResponse (
        UUID id,
        UUID userId,
        Map<UUID, Integer> products
){}
