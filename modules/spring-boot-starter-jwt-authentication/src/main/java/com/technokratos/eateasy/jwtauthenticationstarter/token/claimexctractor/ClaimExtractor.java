package com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor;

import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * Functional interface for type-safe claim extraction from claims map.
 *
 * @param <T> type of claim value to extract
 */
@FunctionalInterface
public interface ClaimExtractor<T> {

    /**
     * Extracts claim value from JWT claims map.
     *
     * @param claims map of JWT claims
     * @return extracted claim value or null
     */
    @Nullable
    T extract(Map<String, Object> claims);
}