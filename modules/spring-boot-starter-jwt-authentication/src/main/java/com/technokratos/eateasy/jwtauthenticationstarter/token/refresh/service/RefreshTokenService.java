package com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service;

/**
 * Complete refresh token lifecycle management service.
 * <p>
 * Combines generation, validation, and revocation capabilities.
 *
 * @see RefreshTokenGeneratorService
 * @see RefreshTokenParserService
 */
public interface RefreshTokenService extends RefreshTokenGeneratorService, RefreshTokenParserService {
    /**
     * Invalidates a specific refresh token instance.
     *
     * @param token       refresh token to revoke
     * @param fingerprint client fingerprint used during token generation
     */
    void invalidate(String token, String fingerprint);
}
