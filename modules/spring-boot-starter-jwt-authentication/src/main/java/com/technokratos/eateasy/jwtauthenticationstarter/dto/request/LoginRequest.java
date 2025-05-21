package com.technokratos.eateasy.jwtauthenticationstarter.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Data transfer object representing login credentials with fingerprint validation.
 * <p>
 * Used for authentication requests where login, password, and client fingerprint
 * are required for security validation.
 * </p>
 */
@SuperBuilder
@Jacksonized
public class LoginRequest extends FingerprintRequest {

    /**
     * Unique user identifier (username/email).
     */
    private final String login;

    /**
     * User's secret credentials (password).
     */
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
