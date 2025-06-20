package com.technokratos.eateasy.jwtauthenticationstarter.security.authenticationprovider;

import com.technokratos.eateasy.jwtauthenticationstarter.security.authentication.RefreshAuthenticationToken;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenAuthenticationProviderTest {
    private static final String JWT = "refresh.jwt";
    private static final String FINGERPRINT = "client-fp";
    private static final String USERNAME = "user";

    @Mock
    private RefreshTokenService tokenService;
    @Mock
    private UserDetailsService userDetailsService;

    private RefreshTokenAuthenticationProvider provider;
    private RefreshAuthenticationToken authentication;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        provider = RefreshTokenAuthenticationProvider.builder()
                .tokenService(tokenService)
                .userDetailsService(userDetailsService)
                .build();

        userDetails = User.withUsername(USERNAME)
                .password("pass")
                .authorities("ROLE_USER")
                .build();
        authentication = RefreshAuthenticationToken.unauthenticated(JWT, FINGERPRINT);
    }

    @Test
    void authenticateValidTokenShouldReturnAuthenticated() throws Exception {
        when(tokenService.extractUsername(JWT)).thenReturn(USERNAME);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);

        Authentication result = provider.authenticate(authentication);

        assertTrue(result.isAuthenticated());
        verify(tokenService).invalidate(JWT, FINGERPRINT);
        assertEquals(userDetails, result.getPrincipal());
    }

    @Test
    void authenticateExpiredTokenShouldThrowsCredentialsExpired() {
        doThrow(new CredentialsExpiredException("Expired"))
                .when(tokenService).validate(JWT, FINGERPRINT);

        assertThrows(CredentialsExpiredException.class, () ->
                provider.authenticate(authentication));
    }

    @Test
    void authenticateInvalidTokenShouldThrowBadCredentials() {
        doThrow(new BadCredentialsException("Invalid"))
                .when(tokenService).validate(JWT, FINGERPRINT);

        assertThrows(BadCredentialsException.class, () ->
                provider.authenticate(authentication));
    }

    @Test
    void authenticateMissingUserShouldThrowInternalError() {
        when(tokenService.extractUsername(JWT)).thenReturn(USERNAME);
        when(userDetailsService.loadUserByUsername(USERNAME))
                .thenThrow(new UsernameNotFoundException("Not found"));

        assertThrows(InternalAuthenticationServiceException.class, () ->
                provider.authenticate(authentication));
    }

    @Test
    void supportsRefreshAuthenticationTokenShouldReturnTrue() {
        assertTrue(provider.supports(RefreshAuthenticationToken.class));
    }

    @Test
    void supportsOtherAuthenticationTypeShouldReturnFalse() {
        assertFalse(provider.supports(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticateWrongAuthenticationTypeShouldReturnNull() {
        Authentication result = provider.authenticate(mock(Authentication.class));
        assertNull(result);
    }

    @Test
    void authenticate_alwaysInvalidatesToken() throws Exception {
        when(tokenService.extractUsername(JWT)).thenReturn(USERNAME);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);

        provider.authenticate(authentication);
        verify(tokenService).invalidate(JWT, FINGERPRINT);
    }
}