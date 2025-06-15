package com.technokratos.eateasy.orderapi;

import java.util.UUID;

public record OrderResponseDto(
        UUID cartId,
        UUID userId,
        String deliveryAddress
) {}

