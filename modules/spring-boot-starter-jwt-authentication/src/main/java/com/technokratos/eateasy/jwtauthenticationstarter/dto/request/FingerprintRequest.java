package com.technokratos.eateasy.jwtauthenticationstarter.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Base request type containing client fingerprint for security context binding.
 * <p>
 * Used as foundation for authentication requests requiring client device identification.
 * </p>
 */
@SuperBuilder
@Jacksonized
@RequiredArgsConstructor
public class FingerprintRequest {

    /**
     * Unique identifier for client browser/device.
     * <p>
     * Used to bind authentication session to specific client instance.
     * </p>
     */
    private final String fingerprint;

    @JsonProperty
    public String fingerprint() {
        return fingerprint;
    }
}
