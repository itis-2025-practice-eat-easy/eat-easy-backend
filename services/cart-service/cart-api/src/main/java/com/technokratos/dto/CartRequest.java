package com.technokratos.dto;

import java.util.Map;
import java.util.UUID;

public record CartRequest(
        UUID userId,
        Map<UUID, Integer> products
) {}
