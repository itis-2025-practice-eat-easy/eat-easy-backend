package com.technokratos.eateasy.orderapi.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderLogResponseDto (
    UUID orderId,
    String status,
    Instant createdAt
){}
