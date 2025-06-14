package com.technokratos.eateasy.jwtauthenticationstarter.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * Base request type containing client fingerprint for security context binding.
 * <p>
 * Used as foundation for authentication requests requiring client device identification.
 * </p>
 */
@SuperBuilder
@Jacksonized
@RequiredArgsConstructor
@Schema(description = "Base request containing a device fingerprint")
public class FingerprintRequest {

    /**
     * Unique identifier for client browser/device.
     * <p>
     * Used to bind authentication session to specific client instance.
     * </p>
     */
    @Schema(description = "Client device fingerprint", requiredMode = REQUIRED, example = "abc123-def456")
    private final String fingerprint;

    @JsonProperty
    public String fingerprint() {
        return fingerprint;
    }
}
