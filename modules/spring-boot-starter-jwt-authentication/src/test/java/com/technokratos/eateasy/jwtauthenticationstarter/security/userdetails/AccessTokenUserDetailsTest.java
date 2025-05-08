package com.technokratos.eateasy.jwtauthenticationstarter.security.userdetails;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccessTokenUserDetailsTest {


    private static final String USERNAME = "username";
    private static final Collection<? extends GrantedAuthority> AUTHORITIES = List.of(new SimpleGrantedAuthority("USER"));

    private AccessTokenUserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = AccessTokenUserDetails.of(USERNAME, AUTHORITIES);
    }

    @Test
    void getPasswordAlwaysShouldReturnNull() {
        assertNull(userDetails.getPassword());
    }

    @Test
    void getUsernameShouldReturnUsername() {
        assertEquals(USERNAME, userDetails.getUsername());
    }

    @Test
    void getAuthoritiesShouldReturnNonNullAuthorities() {
        assertNotNull(userDetails.getAuthorities());
        assertIterableEquals(AUTHORITIES, userDetails.getAuthorities());
    }
}