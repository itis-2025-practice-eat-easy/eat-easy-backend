package com.technokratos.eateasy.jwtauthenticationstarter.security.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessAuthenticationTokenTest {

    private static final String TOKEN = "token";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final List<? extends GrantedAuthority> AUTHORITIES = List.of(new SimpleGrantedAuthority("ROLE_USER"));

    @Spy
    private UserDetails userDetails = User.builder()
            .username(USERNAME)
            .password(PASSWORD)
            .authorities(AUTHORITIES)
            .build();

    @Spy
    private Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

    @Test
    void authenticatedFactoryMethodShouldCreateCorrectInstance() {
        AccessAuthenticationToken token = AccessAuthenticationToken.authenticated(TOKEN, userDetails);

        assertNotNull(token);
        assertEquals(TOKEN, token.getToken());
        assertTrue(token.isAuthenticated());
        assertEquals(userDetails, token.getPrincipal());
        assertEquals(AUTHORITIES, token.getAuthorities());

        verify(userDetails).getAuthorities();

    }

    @Test
    void authenticatedFactoryMethodWithCustomAuthoritiesShouldCreateCorrectInstance() {
        AccessAuthenticationToken token = AccessAuthenticationToken.authenticated(TOKEN, userDetails, authorities);

        assertNotNull(token);
        assertEquals(TOKEN, token.getToken());
        assertTrue(token.isAuthenticated());
        assertEquals(userDetails, token.getPrincipal());
        assertIterableEquals(authorities, token.getAuthorities());
        verify(userDetails, never()).getAuthorities();
    }

    @Test
    void unauthenticatedFactoryMethodShouldCreateCorrectInstance() {
        AccessAuthenticationToken token = AccessAuthenticationToken.unauthenticated(TOKEN);

        assertNotNull(token);
        assertEquals(TOKEN, token.getToken());
        assertEquals(TOKEN, token.getCredentials());
        assertFalse(token.isAuthenticated());
        assertEquals(AccessAuthenticationToken.PRE_AUTH_PRINCIPAL, token.getPrincipal());
        assertTrue(token.getAuthorities().isEmpty());
    }

    @Test
    void setAuthenticatedTrueShouldThrowIllegalArgumentException() {
        AccessAuthenticationToken token = AccessAuthenticationToken.unauthenticated(TOKEN);

        assertThrows(IllegalArgumentException.class, () -> token.setAuthenticated(true));
    }

    @Test
    void setAuthenticatedFalseShouldNotThrowException() {
        AccessAuthenticationToken token = AccessAuthenticationToken.unauthenticated(TOKEN);

        assertDoesNotThrow(() -> token.setAuthenticated(false));
        assertFalse(token.isAuthenticated());
    }
}