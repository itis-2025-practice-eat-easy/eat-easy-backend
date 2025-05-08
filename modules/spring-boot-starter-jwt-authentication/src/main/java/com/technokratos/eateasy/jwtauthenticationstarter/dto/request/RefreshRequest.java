package com.technokratos.eateasy.jwtauthenticationstarter.dto.request;

import lombok.experimental.SuperBuilder;

/**
 * Refresh token request DTO containing token value and client fingerprint.
 * <p>
 * Used for token rotation requests to validate client context.
 * </p>
 */
@SuperBuilder
public class RefreshRequest extends FingerprintRequest{

    private final String refreshToken;

    public RefreshRequest(String fingerprint, String refreshToken) {
        super(fingerprint);
        this.refreshToken = refreshToken;
    }

    public String refreshToken() {
        return refreshToken;
    }
}
