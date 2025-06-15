package com.technokratos.eateasy.orderapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record OrderRequestDto(
        @JsonProperty("user_id")
        UUID userId,
        @JsonProperty("delivery_address")
        String deliveryAddress
) {}
