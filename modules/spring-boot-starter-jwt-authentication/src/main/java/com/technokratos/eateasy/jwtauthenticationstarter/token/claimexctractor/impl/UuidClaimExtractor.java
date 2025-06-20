package com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.impl;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.util.UUID;

/**
 * Extracts UUID values from claims with flexible type handling.
 * <p>
 * Supports both UUID instances and properly formatted UUID strings.
 * Fails gracefully to null for invalid formats.
 * </p>
 */
@Slf4j
@NoArgsConstructor
public class UuidClaimExtractor extends AbstractConfigurableClaimExtractor<UUID> {

    public UuidClaimExtractor(String claimKey) {
        super(claimKey);
    }

    @Nullable
    @Override
    protected UUID mapClaim(Object claim) {
        if (claim instanceof UUID uuid) {
            log.trace("Extracted UUID from claim: {}", uuid);
            return uuid;
        }

        if (claim instanceof String uuidString) {
            try {
                log.trace("Extracted UUID from String claim: {}", uuidString);
                return UUID.fromString(uuidString);
            } catch (IllegalArgumentException e) {
                log.trace("Failed to parse UUID from String claim: {}", uuidString);
                return null;
            }
        }
        log.trace("Claim is not a valid UUID: {}", claim);
        return null;
    }
}
