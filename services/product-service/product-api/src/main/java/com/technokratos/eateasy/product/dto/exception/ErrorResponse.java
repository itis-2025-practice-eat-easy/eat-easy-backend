package com.technokratos.eateasy.product.dto.exception;

import java.time.LocalDateTime;

public record ErrorResponse(int status, String message, String details, LocalDateTime timestamp) {}
