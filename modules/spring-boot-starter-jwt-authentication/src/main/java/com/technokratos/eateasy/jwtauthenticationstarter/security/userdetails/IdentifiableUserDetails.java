package com.technokratos.eateasy.jwtauthenticationstarter.security.userdetails;

import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;

/**
 * Extended {@link UserDetails} with user identifier capability.
 * <p>
 * Provides access to the user's unique persistent identifier while maintaining
 * Spring Security's standard user details contract.
 * </p>
 * @param <T> type of the user identifier, must be {@link Serializable}
 */
public interface IdentifiableUserDetails<T extends Serializable> extends UserDetails {
    T getId();
}
