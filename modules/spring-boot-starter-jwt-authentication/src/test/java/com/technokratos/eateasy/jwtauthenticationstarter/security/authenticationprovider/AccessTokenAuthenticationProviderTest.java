package com.technokratos.eateasy.jwtauthenticationstarter.security.authenticationprovider;

import com.technokratos.eateasy.jwtauthenticationstarter.security.authentication.AccessAuthenticationToken;
import com.technokratos.eateasy.jwtauthenticationstarter.security.userdetails.AccessTokenIdentifiableUserDetails;
import com.technokratos.eateasy.jwtauthenticationstarter.security.userdetails.AccessTokenUserDetails;
import com.technokratos.eateasy.jwtauthenticationstarter.security.userdetails.IdentifiableUserDetails;
import com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.AccessTokenParserService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.ConfigurableClaimExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessTokenAuthenticationProviderTest {

    private static final String VALID_TOKEN = "valid.jwt";
    private static final String USERNAME = "user";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final List<GrantedAuthority> AUTHORITIES = List.of(new SimpleGrantedAuthority("ROLE_USER"));
    private static final String USER_ID_CLAIM = "uid";
    private static final String AUTHORITIES_CLAIM = "authorities";

    @Mock
    private AccessTokenParserService tokenParser;
    @Mock
    private ConfigurableClaimExtractor<Collection<GrantedAuthority>> authoritiesExtractor;
    @Mock
    private ConfigurableClaimExtractor<Serializable> userIdExtractor;

    private AccessTokenAuthenticationProvider provider;

    @BeforeEach
    void setUp() {
        provider = AccessTokenAuthenticationProvider.builder()
                .authoritiesExtractor(authoritiesExtractor)
                .tokenParser(tokenParser)
                .userIdExtractor(userIdExtractor)
                .userIdClaim(USER_ID_CLAIM)
                .authoritiesClaim(AUTHORITIES_CLAIM)
                .build();
    }

    @Test
    void authenticateUnknownAuthenticationShouldReturnNull() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(USERNAME, null);

        assertNull(provider.authenticate(token));
    }

    @Test
    void authenticateValidTokenWithUserIdShouldReturnIdentifiablePrincipal() throws Exception {
        AccessAuthenticationToken token = AccessAuthenticationToken.unauthenticated(VALID_TOKEN);

        doNothing().when(tokenParser).validate(VALID_TOKEN);
        when(tokenParser.extractUsername(VALID_TOKEN)).thenReturn(USERNAME);
        when(tokenParser.extractClaim(any(), same(userIdExtractor))).thenReturn(USER_ID);
        when(tokenParser.extractClaim(any(), same(authoritiesExtractor))).thenReturn(AUTHORITIES);

        Authentication result = provider.authenticate(token);

        assertAll(
                () -> assertTrue(result.isAuthenticated()),
                () -> assertInstanceOf(AccessTokenIdentifiableUserDetails.class, result.getPrincipal()),
                () -> assertEquals(USER_ID, ((IdentifiableUserDetails<?>) result.getPrincipal()).getId()),
                () -> assertEquals(USERNAME, ((IdentifiableUserDetails<?>) result.getPrincipal()).getUsername()),
                () -> assertIterableEquals(AUTHORITIES, ((IdentifiableUserDetails<?>) result.getPrincipal()).getAuthorities())

        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    void authenticateValidTokenWithoutUsernameShouldThrowBadCredentialsException(String username) throws Exception {
        AccessAuthenticationToken token = AccessAuthenticationToken.unauthenticated(VALID_TOKEN);

        doNothing().when(tokenParser).validate(VALID_TOKEN);
        when(tokenParser.extractUsername(VALID_TOKEN)).thenReturn(username);

        assertThrows(BadCredentialsException.class, () -> provider.authenticate(token));
    }

    @Test
    void authenticateValidTokenWhenThrowsIllegalArgumentExceptionShouldThrowInternalAuthenticationServiceException() throws Exception {
        AccessAuthenticationToken token = AccessAuthenticationToken.unauthenticated(VALID_TOKEN);

        doNothing().when(tokenParser).validate(VALID_TOKEN);
        when(tokenParser.extractUsername(any())).thenThrow(new IllegalArgumentException());

        assertThrows(InternalAuthenticationServiceException.class, () -> provider.authenticate(token));
    }

    @Test
    void authenticateValidTokenWithoutUserIdShouldReturnsBasicPrincipal() {
        AccessAuthenticationToken token = AccessAuthenticationToken.unauthenticated(VALID_TOKEN);

        when(tokenParser.extractUsername(VALID_TOKEN)).thenReturn(USERNAME);
        when(tokenParser.extractClaim(any(), same(userIdExtractor))).thenReturn(null);
        when(tokenParser.extractClaim(any(), same(authoritiesExtractor))).thenReturn(AUTHORITIES);
        Authentication result = provider.authenticate(token);


        assertAll(
                () -> assertInstanceOf(AccessTokenUserDetails.class, result.getPrincipal()),
                () -> assertTrue(result.isAuthenticated()),
                () -> assertEquals(USERNAME, ((UserDetails) result.getPrincipal()).getUsername()),
                () -> assertIterableEquals(AUTHORITIES, ((UserDetails) result.getPrincipal()).getAuthorities())
        );
    }

    @Test
    void authenticateInvalidTokenShouldThrowBadCredentialsException() {
        AccessAuthenticationToken token = AccessAuthenticationToken.unauthenticated("invalid");

        doThrow(new BadCredentialsException("")).when(tokenParser).validate(any());

        assertThrows(BadCredentialsException.class, () -> provider.authenticate(token));
    }

    @Test
    void authenticateExpiredTokenShouldThrowCredentialsExpired() {
        AccessAuthenticationToken token = AccessAuthenticationToken.unauthenticated(VALID_TOKEN);

        doThrow(new CredentialsExpiredException("")).when(tokenParser).validate(any());

        assertThrows(CredentialsExpiredException.class, () -> provider.authenticate(token));
    }

    @Test
    void supportsAccessTokenAuthenticationShouldReturnsTrue() {
        assertTrue(provider.supports(AccessAuthenticationToken.class));
    }

    @Test
    void supportsOtherAuthenticationShouldReturnFalse() {
        assertFalse(provider.supports(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void postConstructShouldInitializeClaimNames() {
        provider.init();

        verify(authoritiesExtractor).claimName(AUTHORITIES_CLAIM);
        verify(userIdExtractor).claimName(USER_ID_CLAIM);
    }
}