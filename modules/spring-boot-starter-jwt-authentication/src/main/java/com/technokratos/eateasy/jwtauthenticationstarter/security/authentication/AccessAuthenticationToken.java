package com.technokratos.eateasy.jwtauthenticationstarter.security.authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;

/**
 * Authentication token for JWT access token processing.
 * <p>
 * Represents both authenticated and unauthenticated states for access token
 * authentication workflow.
 */
public class AccessAuthenticationToken extends AbstractJwtTokenAuthenticationToken {

    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private AccessAuthenticationToken(String token) {
        super(token);
    }

    private AccessAuthenticationToken(String token, UserDetails userDetails, Collection<? extends GrantedAuthority> authorities) {
        super(token, userDetails, authorities);
    }

    /**
     * Creates unauthenticated access token instance
     * @param token raw access token
     * @return new unauthenticated token
     */
    public static AccessAuthenticationToken unauthenticated(String token) {
        return new AccessAuthenticationToken(token);
    }

    /**
     * Creates authenticated token with minimal authorities
     * @param token validated access token
     * @param userDetails resolved user details
     * @return authenticated token
     */
    public static AccessAuthenticationToken authenticated(String token, UserDetails userDetails) {
        return authenticated(token, userDetails, userDetails.getAuthorities());
    }


    /**
     * Creates authenticated token with custom authorities
     * @param token validated access token
     * @param userDetails resolved user details
     * @param authorities granted authorities
     * @return authenticated token
     */
    public static AccessAuthenticationToken authenticated(String token, UserDetails userDetails,
                                                          Collection<? extends GrantedAuthority> authorities) {
        return new AccessAuthenticationToken(token, userDetails, authorities);
    }

}
