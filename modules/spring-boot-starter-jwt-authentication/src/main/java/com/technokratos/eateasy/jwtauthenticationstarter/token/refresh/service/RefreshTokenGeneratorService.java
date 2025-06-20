package com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service interface for generating refresh tokens with fingerprint binding.
 */
public interface RefreshTokenGeneratorService {
    /**
     * Generates a refresh token bound to client fingerprint.
     *
     * @param userDetails authenticated user details
     * @param fingerprint client device fingerprint for token binding
     * @return generated refresh token
     */
    String generate(UserDetails userDetails, String fingerprint);
}