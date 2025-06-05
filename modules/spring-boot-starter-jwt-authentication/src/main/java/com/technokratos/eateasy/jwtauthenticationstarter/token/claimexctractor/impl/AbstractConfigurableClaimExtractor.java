package com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.impl;

import com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.ConfigurableClaimExtractor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Base class for configurable claim extractors with mutable claim key.
 * <p>
 * Combines {@link ConfigurableClaimExtractor} interface with template extraction logic.
 *
 * @see AbstractClaimExtractor
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractConfigurableClaimExtractor<T> extends AbstractClaimExtractor<T>
        implements ConfigurableClaimExtractor<T> {

    protected String claimName;

    /**
     * {@inheritDoc}
     */
    @Override
    public void claimName(String claimName) {
        this.claimName = claimName;
    }

    @Override
    protected String getClaimKey() {
        return claimName;
    }
}
