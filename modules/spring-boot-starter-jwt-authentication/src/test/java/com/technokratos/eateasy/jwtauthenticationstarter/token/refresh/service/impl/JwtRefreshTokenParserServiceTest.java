package com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.impl;

import com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token;
import com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.ConfigurableClaimExtractor;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.model.RefreshTokenEntity;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.repository.RefreshTokenRepository;
import com.technokratos.eateasy.jwtservice.JwtParserService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token.TokenType.REFRESH;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtRefreshTokenParserServiceTest {

    private static final String USERNAME = "username";
    private static final String VALID_TOKEN = "valid.token";
    private static final String INVALID_TOKEN = "invalid.token";
    private static final String MISSING_TOKEN = "missing-token";
    private static final String TOKEN_WITHOUT_ID = "token-without-id";
    private static final String TOKEN_WITH_EXCEPTION = "token-with-exception";
    private static final String FINGERPRINT = "fingerprint";
    private static final String FINGERPRINT_HASH = "hashed-fingerprint";
    private static final UUID TOKEN_ID = UUID.randomUUID();
    private static final UUID MISSING_TOKEN_ID = UUID.randomUUID();

    @Mock(strictness = Mock.Strictness.LENIENT)
    @Token(REFRESH)
    private JwtParserService jwtParser;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private PasswordEncoder passwordEncoder;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private RefreshTokenRepository repository;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private ConfigurableClaimExtractor<UUID> refreshTokenIdExtractor;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private RefreshTokenEntity refreshTokenEntity;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private Claims claimsWithId;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private Claims claimsWithoutId;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private Claims missingTokenClaims;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private Claims tokenWithExceptionClaims;

    private JwtRefreshTokenParserService tokenParser;

    @BeforeEach
    void setup() {
        tokenParser = JwtRefreshTokenParserService.builder()
                .jwtParser(jwtParser)
                .passwordEncoder(passwordEncoder)
                .repository(repository)
                .refreshTokenIdExtractor(refreshTokenIdExtractor)
                .build();
        tokenParser.init();
        when(jwtParser.extractUsername(VALID_TOKEN)).thenReturn(USERNAME);
        when(refreshTokenIdExtractor.extract(claimsWithId)).thenReturn(TOKEN_ID);
        when(refreshTokenIdExtractor.extract(claimsWithoutId)).thenReturn(null);
        when(refreshTokenIdExtractor.extract(missingTokenClaims)).thenReturn(MISSING_TOKEN_ID);
        when(refreshTokenIdExtractor.extract(tokenWithExceptionClaims)).thenThrow(new IllegalArgumentException());
        when(passwordEncoder.matches(FINGERPRINT, FINGERPRINT_HASH)).thenReturn(true);
        when(refreshTokenEntity.getFingerprint()).thenReturn(FINGERPRINT_HASH);
        when(repository.findById(TOKEN_ID)).thenReturn(Optional.of(refreshTokenEntity));
        when(repository.findById(MISSING_TOKEN_ID)).thenReturn(Optional.empty());
        doNothing().when(jwtParser).validate(VALID_TOKEN);
        doNothing().when(jwtParser).validate(TOKEN_WITHOUT_ID);
        doNothing().when(jwtParser).validate(MISSING_TOKEN);
        doNothing().when(jwtParser).validate(TOKEN_WITH_EXCEPTION);
        doThrow(new BadCredentialsException("Invalid token")).when(jwtParser).validate(INVALID_TOKEN);
        when(jwtParser.extractAllClaims(VALID_TOKEN)).thenReturn(claimsWithId);
        when(jwtParser.extractAllClaims(TOKEN_WITHOUT_ID)).thenReturn(claimsWithoutId);
        when(jwtParser.extractAllClaims(MISSING_TOKEN)).thenReturn(missingTokenClaims);
        when(jwtParser.extractAllClaims(TOKEN_WITH_EXCEPTION)).thenReturn(tokenWithExceptionClaims);
    }

    @Test
    void validateValidTokenAndFingerprintShouldSucceed() {
        assertDoesNotThrow(() -> tokenParser.validate(VALID_TOKEN, FINGERPRINT));
        verify(refreshTokenIdExtractor).claimName(RefreshTokenServiceConstants.REFRESH_TOKEN_ID);
    }

    @Test
    void extractUsernameShouldDelegateToJwtParser() {
        String username = tokenParser.extractUsername(VALID_TOKEN);
        assertEquals(USERNAME, username);
        verify(jwtParser).extractUsername(VALID_TOKEN);
    }

    @Test
    void validateMissingTokenShouldThrowCredentialsExpiredException() {
        assertThrows(CredentialsExpiredException.class, () -> tokenParser.validate(MISSING_TOKEN, FINGERPRINT));
        verify(refreshTokenEntity, never()).getFingerprint();
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void validateFingerprintMismatchShouldThrowsBadCredentialsException() {
        assertThrows(BadCredentialsException.class,
                () -> tokenParser.validate(VALID_TOKEN, "invalid-fingerprint"));

    }

    @Test
    void validateNullFingerprintsShouldSucceed() {
        when(refreshTokenEntity.getFingerprint()).thenReturn(null);
        assertDoesNotThrow(() -> tokenParser.validate(VALID_TOKEN, null));
    }

    @Test
    void validateProvidedNullFingerprintShouldThrowBadCredentialsException() {
        assertThrows(BadCredentialsException.class,
                () -> tokenParser.validate(VALID_TOKEN, null));
    }

    @Test
    void validateExpectedNullFingerprintShouldThrowBadCredentialsException() {
        when(refreshTokenEntity.getFingerprint()).thenReturn(null);
        assertThrows(BadCredentialsException.class,
                () -> tokenParser.validate(VALID_TOKEN, FINGERPRINT));
    }


    @Test
    void extractIdInternalInvalidTokenShouldThrowInternalAuthenticationServiceException() {
        assertThrows(InternalAuthenticationServiceException.class,
                () -> tokenParser.validate(TOKEN_WITHOUT_ID, FINGERPRINT));
        verify(repository, never()).findById(any());
    }

    @Test
    void validateBaseTokenValidationFailureShouldPropagateException() {
        assertThrows(BadCredentialsException.class,
                () -> tokenParser.validate(INVALID_TOKEN, FINGERPRINT));
        verify(refreshTokenIdExtractor, never()).extract(any());
        verify(repository, never()).findById(any());
    }

    @Test
    void validateTokenWithExceptionShouldThrowInternalAuthenticationServiceException() {
        assertThrows(InternalAuthenticationServiceException.class,
                () -> tokenParser.validate(TOKEN_WITH_EXCEPTION, FINGERPRINT));
        verify(repository, never()).findById(any());
    }

}