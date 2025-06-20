package com.technokratos.eateasy.jwtauthenticationstarter.token.access.service;

import com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.ClaimExtractor;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

/**
 * Service interface for parsing and validating access tokens.
 */
public interface AccessTokenParserService {
    /**
     * Extracts username (subject) from access token.
     *
     * @param token access token to parse
     * @return username associated with the token
     * @throws IllegalArgumentException for malformed or invalid tokens
     */
    String extractUsername(String token) throws IllegalArgumentException;

    /**
     * Validates access token integrity and authenticity.
     *
     * @param token access token to validate
     * @throws AuthenticationException for invalid/expired tokens
     */
    void validate(String token) throws AuthenticationException;

    /**
     * Extracts all claims from access token body.
     *
     * @param token valid access token
     * @return map containing all token claims
     * @throws IllegalArgumentException for invalid tokens
     */
    Map<String, Object> extractAllClaims(String token) throws IllegalArgumentException;

    /**
     * Extracts specific claim using provided claims processor.
     *
     * @param token access token to parse
     * @param claimExtractor processor for extracting target claim
     * @return extracted claim value or null if not present
     * @param <T> type of claim value
     * @throws IllegalArgumentException for invalid tokens
     */
    default  <T> @Nullable T extractClaim(String token, ClaimExtractor<? extends T> claimExtractor) throws IllegalArgumentException {
        return claimExtractor.extract(extractAllClaims(token));
    }

}