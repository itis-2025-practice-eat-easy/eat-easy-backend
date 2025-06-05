package com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.impl;

import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.repository.RefreshTokenRepository;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenGeneratorService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompositeRefreshTokenServiceTest {

    private static final String TOKEN = "refresh.token";
    private static final String INVALID_TOKEN = "invalid.refresh.token";
    private static final String FINGERPRINT = "client-fp";
    private static final String USERNAME = "username";
    private static final UUID TOKEN_ID = UUID.randomUUID();

    @Mock(strictness = Mock.Strictness.LENIENT)
    private RefreshTokenGeneratorService generator;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private RefreshTokenParserService parser;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private RefreshTokenRepository repository;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private UserDetails userDetails;
    private CompositeRefreshTokenService service;

    @BeforeEach
    void setUp() {
        service = CompositeRefreshTokenService.builder()
                .generator(generator)
                .parser(parser)
                .repository(repository)
                .build();

        when(generator.generate(userDetails, FINGERPRINT)).thenReturn(TOKEN);
        when(parser.extractUsername(TOKEN)).thenReturn(USERNAME);
        when(parser.extractId(TOKEN)).thenReturn(TOKEN_ID);
        doThrow(new CredentialsExpiredException("Token expired")).when(parser).validate(eq(INVALID_TOKEN), any());
    }

    @Test
    void generateShouldDelegateToGenerator() {
        assertEquals(TOKEN, service.generate(userDetails, FINGERPRINT));

        verify(generator).generate(userDetails, FINGERPRINT);
    }

    @Test
    void extractUsernameShouldDelegateToParser() {
        assertEquals(USERNAME, service.extractUsername(TOKEN));

        verify(parser).extractUsername(TOKEN);
    }

    @Test
    void validateShouldDelegateToParser() {
        service.validate(TOKEN, FINGERPRINT);

        verify(parser).validate(TOKEN, FINGERPRINT);
    }

    @Test
    void extractIdShouldDelegateToParser() {
        assertEquals(TOKEN_ID, service.extractId(TOKEN));

        verify(parser).extractId(TOKEN);
    }

    @Test
    void invalidateValidTokenShouldDeleteFromRepository() {
        service.invalidate(TOKEN, FINGERPRINT);
        verify(repository).deleteById(TOKEN_ID);
    }

    @Test
    void invalidateAlreadyInvalidatedShouldDoNothing() {

        service.invalidate(INVALID_TOKEN, FINGERPRINT);
        verify(repository, never()).deleteById(any());
        verify(parser, never()).extractId(any());
    }
}