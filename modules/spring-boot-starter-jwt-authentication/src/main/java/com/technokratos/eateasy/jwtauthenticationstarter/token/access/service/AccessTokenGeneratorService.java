package com.technokratos.eateasy.jwtauthenticationstarter.token.access.service;

import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

/**
 * Service interface for generating access tokens.
 */
public interface AccessTokenGeneratorService {
    /**
     * Generates a signed access token for authenticated user with additional claims.
     *
     * @param userDetails authenticated user details
     * @param extraClaims non-null map of custom claims to include in token
     * @return signed JWT access token
     */
    String generate(UserDetails userDetails, @NonNull Map<String, Object> extraClaims);
}