package com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.impl;

import com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token;
import com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.ClaimExtractor;
import com.technokratos.eateasy.jwtservice.JwtParserService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Map;

import static com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token.TokenType.ACCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAccessTokenParserServiceTest {
    private static final String VALID_TOKEN = "valid.token.xyz";
    private static final String INVALID_TOKEN = "invalid.token";
    private static final String USERNAME = "username";

    @Mock(strictness = Mock.Strictness.LENIENT)
    @Token(ACCESS)
    private JwtParserService jwtParser;

    @InjectMocks
    private JwtAccessTokenParserService tokenParser;

    @BeforeEach
    void setUp() {
        when(jwtParser.extractUsername(VALID_TOKEN)).thenReturn(USERNAME);
        doNothing().when(jwtParser).validate(VALID_TOKEN);
        doThrow(new BadCredentialsException("Invalid token")).when(jwtParser).validate(INVALID_TOKEN);

    }

    @Test
    void extractUsernameShouldDelegateToParserService() {

        String username = tokenParser.extractUsername(VALID_TOKEN);

        assertEquals(USERNAME, username);
        verify(jwtParser).extractUsername(VALID_TOKEN);
    }

    @Test
    void validateWithValidTokenSucceeds() {
        doNothing().when(jwtParser).validate(VALID_TOKEN);

        assertDoesNotThrow(() -> tokenParser.validate(VALID_TOKEN));
        verify(jwtParser).validate(VALID_TOKEN);
    }

    @Test
    void validateShouldPropagateAuthenticationException() {
        assertThrows(BadCredentialsException.class, () -> tokenParser.validate(INVALID_TOKEN));
    }

    @Test
    void extractAllClaimsShouldDelegateToParserService() {
        Claims mockClaims = mock(Claims.class);
        when(jwtParser.extractAllClaims(VALID_TOKEN)).thenReturn(mockClaims);

        Map<String, Object> claims = tokenParser.extractAllClaims(VALID_TOKEN);

        assertEquals(mockClaims, claims);
        verify(jwtParser).extractAllClaims(VALID_TOKEN);
    }

    @Test
    @SuppressWarnings("unchecked")
    void extractClaimShouldDelegateAllClaimsToClaimExtractor() {
        Claims mockClaims = mock(Claims.class);
        ClaimExtractor<String> claimExtractor = mock(ClaimExtractor.class);
        String claimValue = "value";

        when(claimExtractor.extract(mockClaims)).thenReturn(claimValue);
        when(jwtParser.extractAllClaims(VALID_TOKEN)).thenReturn(mockClaims);
        when(claimExtractor.extract(mockClaims)).thenReturn(claimValue);
        String claim = tokenParser.extractClaim(VALID_TOKEN, claimExtractor);

        assertEquals(claimValue, claim);
        verify(jwtParser).extractAllClaims(VALID_TOKEN);
    }

}