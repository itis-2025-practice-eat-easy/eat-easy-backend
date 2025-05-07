package com.technokratos.eateasy.jwtauthenticationstarter.security.authentication;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RefreshAuthenticationTokenTest {


    private static final String TOKEN = "token";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String FINGERPRINT = "fingerprint";
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
        RefreshAuthenticationToken token = RefreshAuthenticationToken.authenticated(TOKEN, userDetails);

        assertNotNull(token);
        assertEquals(TOKEN, token.getToken());
        assertTrue(token.isAuthenticated());
        assertEquals(userDetails, token.getPrincipal());
        assertTrue(token.getAuthorities().isEmpty());

    }

    @Test
    void authenticatedFactoryMethodWithCustomAuthoritiesShouldCreateCorrectInstance() {
        RefreshAuthenticationToken token = RefreshAuthenticationToken.authenticated(TOKEN, userDetails);

        assertNotNull(token);
        assertEquals(TOKEN, token.getToken());
        assertTrue(token.isAuthenticated());
        assertEquals(userDetails, token.getPrincipal());
        assertTrue(token.getAuthorities().isEmpty());
    }

    @Test
    void unauthenticatedFactoryMethodShouldCreateCorrectInstance() {
        RefreshAuthenticationToken token = RefreshAuthenticationToken.unauthenticated(TOKEN, FINGERPRINT);

        assertNotNull(token);
        assertEquals(TOKEN, token.getToken());
        assertEquals(TOKEN, token.getCredentials());
        assertEquals(FINGERPRINT, token.getFingerprint());
        assertFalse(token.isAuthenticated());
        assertEquals(RefreshAuthenticationToken.PRE_AUTH_PRINCIPAL, token.getPrincipal());
        assertTrue(token.getAuthorities().isEmpty());
    }

    @Test
    void setAuthenticatedTrueShouldThrowIllegalArgumentException() {
        RefreshAuthenticationToken token = RefreshAuthenticationToken.unauthenticated(TOKEN, FINGERPRINT);

        assertThrows(IllegalArgumentException.class, () -> token.setAuthenticated(true));
    }

    @Test
    void setAuthenticatedFalseShouldNotThrowException() {
        RefreshAuthenticationToken token = RefreshAuthenticationToken.unauthenticated(TOKEN, FINGERPRINT);

        assertDoesNotThrow(() -> token.setAuthenticated(false));
        assertFalse(token.isAuthenticated());
    }

}