package com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.impl;

import com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.ClaimExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.Objects;

/**
 * Template implementation for claim extraction with common validation logic.
 * <p>
 * Provides base functionality for:
 * <ol>
 *   <li>Claim key existence verification</li>
 *   <li>Type conversion and fallback values</li>
 * </ol>
 * </p>
 * @param <T> type of claim value to extract
 *
 * @see ClaimExtractor
 */
@Slf4j
public abstract class AbstractClaimExtractor<T> implements ClaimExtractor<T> {

    /**
     * {@inheritDoc}
     * <p>
     * Implements extraction workflow:
     * <ol>
     *   <li>Get configured claim key</li>
     *   <li>Check claim existence</li>
     *   <li>Map raw claim value to target type</li>
     * </ol>
     * @throws IllegalStateException if claim key not configured
     */
    @Override
    @Nullable
    public T extract(Map<String, Object> claims) throws IllegalStateException {
        String claimKey = getClaimKey();

        if (Objects.isNull(claimKey)) {
            log.error("Claim key is not set");
            throw new IllegalStateException("Claim key is not set");
        }

        Object claim = claims.get(claimKey);
        if (Objects.isNull(claim)) {
            log.trace("Claims does not contain claim with key: {}", getClaimKey());
            return defaultValue();
        }

        return mapClaim(claim);
    }

    /**
     * @return configured JWT claim key
     */
    protected abstract String getClaimKey();

    /**
     * Provides fallback value when claim is missing
     * @return null by default
     */
    protected T defaultValue() {
        return null;
    }

    /**
     * Converts raw claim value to target type
     * @param claim raw claim value from token
     * @return mapped value or null
     */
    protected abstract T mapClaim(Object claim);
}
