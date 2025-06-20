package com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service;

import org.springframework.security.core.AuthenticationException;

import java.util.UUID;

/**
 * Service interface for validating and parsing fingerprint-bound refresh tokens.
 */
public interface RefreshTokenParserService {
    /**
     * Extracts username from refresh token claims.
     *
     * @param token refresh token to parse
     * @return associated username
     * @throws IllegalArgumentException for malformed of invalid tokens
     */
    String extractUsername(String token) throws IllegalArgumentException;

    /**
     * Extracts unique token identifier from refresh token claims.
     *
     * @param token refresh token to parse
     * @return {@link UUID} token identifier
     * @throws IllegalArgumentException for invalid token format
     */
    UUID extractId(String token) throws IllegalArgumentException;

    /**
     * Validates refresh token integrity and fingerprint match.
     *
     * @param token refresh token to validate
     * @param fingerprint client device fingerprint to verify
     * @throws AuthenticationException for invalid/expired tokens or fingerprint mismatch
     */
    void validate(String token, String fingerprint) throws AuthenticationException;
}
