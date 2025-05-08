package com.technokratos.eateasy.jwtauthenticationstarter.security.userdetails;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccessTokenIdentifiableUserDetailsTest {

    private static final String USERNAME = "username";
    private static final UUID ID = UUID.randomUUID();
    private static final Collection<? extends GrantedAuthority> AUTHORITIES = List.of(new SimpleGrantedAuthority("USER"));

    private AccessTokenIdentifiableUserDetails<UUID> userDetails;

    @BeforeEach
    void setUp() {
        userDetails = AccessTokenIdentifiableUserDetails.of(ID, USERNAME, AUTHORITIES);
    }

    @Test
    void getIdShouldReturnId() {
        assertEquals(ID, userDetails.getId());
    }
}