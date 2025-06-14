package com.technokratos.eateasy.jwtauthenticationstarter.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Refresh token request DTO containing token value and client fingerprint.
 * <p>
 * Used for token rotation requests to validate client context.
 * </p>
 */
@SuperBuilder
@Jacksonized
@Schema(description = "Refresh token request containing the refresh token and device fingerprint")
public class RefreshRequest extends FingerprintRequest{

    @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private final String refreshToken;

    public RefreshRequest(String fingerprint, String refreshToken) {
        super(fingerprint);
        this.refreshToken = refreshToken;
    }

    @JsonProperty
    public String refreshToken() {
        return refreshToken;
    }
}
