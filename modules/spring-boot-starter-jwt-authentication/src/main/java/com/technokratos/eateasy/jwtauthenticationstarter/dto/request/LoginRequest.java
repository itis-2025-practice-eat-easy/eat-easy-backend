package com.technokratos.eateasy.jwtauthenticationstarter.dto.request;

import lombok.experimental.SuperBuilder;

/**
 * Data transfer object representing login credentials with fingerprint validation.
 * <p>
 * Used for authentication requests where login, password, and client fingerprint
 * are required for security validation.
 * </p>
 */
@SuperBuilder
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

    public String login() {
        return login;
    }

    public String password() {
        return password;
    }
}
