package com.technokratos.eateasy.orderapi.dto;

import java.util.UUID;

public record OrderResponseDto(
        UUID cartId,
        UUID userId,
        String deliveryAddress
) {}

