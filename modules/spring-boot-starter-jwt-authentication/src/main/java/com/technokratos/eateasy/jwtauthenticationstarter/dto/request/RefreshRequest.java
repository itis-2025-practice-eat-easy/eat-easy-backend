package com.technokratos.eateasy.jwtauthenticationstarter.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class RefreshRequest extends FingerprintRequest{

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
