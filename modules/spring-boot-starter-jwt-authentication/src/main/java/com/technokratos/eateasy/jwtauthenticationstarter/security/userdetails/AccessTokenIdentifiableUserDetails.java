package com.technokratos.eateasy.jwtauthenticationstarter.security.userdetails;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

/**
 * UserDetails implementation with persistent user identifier for access token context.
 * <p>
 * Extends {@link AccessTokenUserDetails} with ID capability for audit trails and
 * authorization checks requiring persistent user references.
 * </p>
 * @param <T> type of persistent user identifier (must be {@link Serializable})
 * @see IdentifiableUserDetails
 */
public class AccessTokenIdentifiableUserDetails<T extends Serializable> extends AccessTokenUserDetails
        implements IdentifiableUserDetails<T> {


    private final T userId;

    private AccessTokenIdentifiableUserDetails(T userId, String username,
                                               Collection<? extends GrantedAuthority> authorities) {
        super(username, authorities);
        this.userId = userId;
    }

    @Override
    public T getId() {
        return userId;
    }

    /**
     * Creates instance with provided identifier and authorities
     * @param userId persistent user identifier
     * @param authorities nullable collection of granted authorities
     */
    public static <T extends Serializable> AccessTokenIdentifiableUserDetails<T> of(T userId, String username,
                                                                                    Collection<? extends GrantedAuthority> authorities) {
        return new AccessTokenIdentifiableUserDetails<>(userId, username, authorities);
    }
}
