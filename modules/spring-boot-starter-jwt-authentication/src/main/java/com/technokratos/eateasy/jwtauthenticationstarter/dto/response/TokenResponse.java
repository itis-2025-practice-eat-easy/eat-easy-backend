package com.technokratos.eateasy.jwtauthenticationstarter.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * Authentication response containing generated JWT tokens.
 */
@Builder
@Schema(description = "Response containing access and refresh tokens")
public record TokenResponse(
        @Schema(description = "JWT access token", requiredMode = REQUIRED, example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String access,

        @Schema(description = "JWT refresh token", requiredMode = REQUIRED, example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String refresh) {
    @Override
    public String toString() {
        return "TokenResponse{access='%s', refresh='%s'}".formatted(access, refresh);
    }
}
