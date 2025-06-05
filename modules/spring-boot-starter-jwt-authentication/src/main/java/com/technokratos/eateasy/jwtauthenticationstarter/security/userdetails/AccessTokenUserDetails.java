package com.technokratos.eateasy.jwtauthenticationstarter.security.userdetails;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Lightweight UserDetails implementation for JWT access token authentication.
 * <p>
 * Contains username and authorities without password storage. Suitable for token-based
 * authentication where password validation isn't required post-authentication.
 * <p>
 * @see UserDetails
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessTokenUserDetails implements UserDetails {

    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * {@inheritDoc}
     * <p>
     * Returns empty list if no authorities provided
     * <p>
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Objects.requireNonNullElse(authorities, Collections.emptyList());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Factory method for creating instances
     * @param authorities nullable collection of granted authorities
     */
    public static AccessTokenUserDetails of(String username, Collection<? extends GrantedAuthority> authorities) {
        return new AccessTokenUserDetails(username, authorities);
    }
}
