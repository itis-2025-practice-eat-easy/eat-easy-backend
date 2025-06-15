package com.technokratos.eateasy.orderapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderRequestDto(
        @NotNull UUID userId,
        @NotBlank String deliveryAddress
) {}
