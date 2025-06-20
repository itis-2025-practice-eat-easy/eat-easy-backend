package com.technokratos.eateasy.jwtauthenticationstarter.security.authentication;

import org.springframework.lang.Nullable;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collections;

/**
 * Authentication token for refresh token processing with fingerprint binding.
 * <p>
 * Manages additional fingerprint parameter for refresh token validation.
 * </p>
 */
public class RefreshAuthenticationToken extends AbstractJwtTokenAuthenticationToken {

    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final String fingerprint;

    private RefreshAuthenticationToken(String token, String fingerprint) {
        super(token);
        this.fingerprint = fingerprint;
    }

    private RefreshAuthenticationToken(String token, UserDetails userDetails) {
        super(token, userDetails, Collections.emptyList());
        this.fingerprint = null;
    }

    /**
     * @return client fingerprint for validation or null if authenticated
     */
    @Nullable
    public String getFingerprint() {
        return fingerprint;
    }

    /**
     * Creates unauthenticated refresh token with client fingerprint
     * @param token raw refresh token
     * @param fingerprint client device fingerprint
     * @return unauthenticated token
     */
    public static RefreshAuthenticationToken unauthenticated(String token, String fingerprint) {
        return new RefreshAuthenticationToken(token, fingerprint);
    }

    /**
     * Creates authenticated refresh token instance
     * @param token validated refresh token
     * @param principal authenticated user
     * @return authenticated token
     */
    public static RefreshAuthenticationToken authenticated(String token, UserDetails principal) {
        return new RefreshAuthenticationToken(token, principal);
    }
}