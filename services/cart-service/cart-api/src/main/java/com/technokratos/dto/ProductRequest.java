package com.technokratos.dto;

import java.util.UUID;

public record ProductRequest(
        UUID id,
        Integer quantity
) {}
