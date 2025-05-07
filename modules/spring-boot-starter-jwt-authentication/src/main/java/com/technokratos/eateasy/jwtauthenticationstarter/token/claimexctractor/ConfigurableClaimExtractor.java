package com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor;

/**
 * Extension of {@link ClaimExtractor} with runtime-configurable claim name capability.
 * <p>
 * Allows dynamic specification of claim key for extraction implementations.
 * </p>
 */
public interface ConfigurableClaimExtractor<T> extends ClaimExtractor<T> {
    /**
     * Sets the target claim name for extraction
     * @param claimName claim key to look up
     */
    void claimName(String claimName);
}
