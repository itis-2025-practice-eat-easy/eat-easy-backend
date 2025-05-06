package com.technokratos.eateasy.jwtservice;

import io.jsonwebtoken.Claims;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;

import java.util.function.Function;

/**
 * Service interface for JWT token parsing and validation operations.
 */
public interface JwtParserService {

    /**
     * Extracts username (subject) from JWT token.
     *
     * @param token JWT token to parse
     * @return username contained in the token
     * @throws IllegalArgumentException if token is malformed or invalid
     */
    default String extractUsername(String token) throws IllegalArgumentException {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Validates JWT token integrity and authenticity.
     *
     * @param token JWT token to validate
     * @throws AuthenticationException if token is expired, malformed or invalid
     */
    void validate(String token) throws AuthenticationException;

    /**
     * Extracts specific claim from JWT token.
     *
     * @param token JWT token to parse
     * @param claimExtractor function to extract desired claim from claims set
     * @return extracted claim value or null if claim doesn't exist
     * @param <T> type of the claim to extract
     * @throws IllegalArgumentException if token is malformed or invalid
     */
    default  <T> @Nullable T extractClaim(String token, Function<Claims, T> claimExtractor) throws IllegalArgumentException {
        return claimExtractor.apply(extractAllClaims(token));
    }

    /**
     * Extracts all claims from JWT token.
     *
     * @param token JWT token to parse
     * @return map containing all token claims
     * @throws IllegalArgumentException if token is malformed or invalid
     */
    Claims extractAllClaims(String token) throws IllegalArgumentException;
}
