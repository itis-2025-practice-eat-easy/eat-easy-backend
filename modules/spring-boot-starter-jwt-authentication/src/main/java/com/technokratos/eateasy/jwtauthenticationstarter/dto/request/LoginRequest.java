package com.technokratos.eateasy.jwtauthenticationstarter.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * Data transfer object representing login credentials with fingerprint validation.
 * <p>
 * Used for authentication requests where login, password, and client fingerprint
 * are required for security validation.
 * </p>
 */
@SuperBuilder
@Jacksonized
@Schema(description = "Login request containing user credentials and fingerprint")
public class LoginRequest extends FingerprintRequest {

    /**
     * Unique user identifier (username/email).
     */
    @Schema(description = "User login", requiredMode = REQUIRED, example = "user@example.com")
    private final String login;

    /**
     * User's secret credentials (password).
     */
    @Schema(description = "User password", requiredMode = REQUIRED, example = "P@ssw0rd123")
    private final String password;

    public LoginRequest(String fingerprint, String login, String password) {
        super(fingerprint);
        this.login = login;
        this.password = password;
    }

    @JsonProperty
    public String login() {
        return login;
    }

    @JsonProperty
    public String password() {
        return password;
    }
}
