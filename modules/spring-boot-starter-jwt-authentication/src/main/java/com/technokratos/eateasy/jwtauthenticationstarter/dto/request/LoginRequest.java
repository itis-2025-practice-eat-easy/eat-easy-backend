package com.technokratos.eateasy.jwtauthenticationstarter.dto.request;

import lombok.Builder;

/**
 * Data transfer object representing login credentials with fingerprint validation.
 * <p>
 * Used for authentication requests where login, password, and client fingerprint
 * are required for security validation.
 * </p>
 * @param login       unique user identifier (username/email)
 * @param password    user's secret credentials
 * @param fingerprint client browser/device fingerprint for session binding
 */
@Builder
public record LoginRequest(String login, String password, String fingerprint) {
}
