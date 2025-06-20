package com.technokratos.eateasy.jwtauthenticationstarter.security.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

/**
 * Base authentication token for token-based authentication flow.
 * <p>
 * Manages token storage and authentication state transitions. Enforces proper
 * authentication flow by preventing manual authentication state changes.
 * </p>
 *
 * @see AbstractAuthenticationToken
 */
public abstract class AbstractJwtTokenAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * Principal identifier for pre-authenticated tokens
     */
    public static final String PRE_AUTH_PRINCIPAL = "JWT_PRE_AUTH_USER";

    private final String token;
    private final Object principal;


    /**
     * Creates unauthenticated token with raw token
     * @param token raw token string
     * @throws NullPointerException if token is null
     */
    protected AbstractJwtTokenAuthenticationToken(String token) {
        super(null);
        this.token = Objects.requireNonNull(token, "JWT cannot be null");
        this.principal = PRE_AUTH_PRINCIPAL;
        super.setAuthenticated(false);
    }


    /**
     * Creates authenticated token with resolved security context
     * @param token raw token string
     * @param userDetails authenticated user details
     * @param authorities granted authorities
     * @throws NullPointerException if userDetails is null
     */
    protected AbstractJwtTokenAuthenticationToken(String token, UserDetails userDetails, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.principal = Objects.requireNonNull(userDetails, "UserDetails cannot be null");
        super.setAuthenticated(true);
    }

    /**
     * @return raw token or null if cleared
     */
    @Nullable
    public String getToken() {
        return token;
    }

    @Override
    @Nullable
    public Object getCredentials() {
        return getToken();
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException if trying to set authenticated to true
     */
    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set authentication status to true - use factory methods for authenticated tokens");
        }
        super.setAuthenticated(false);
    }
}
