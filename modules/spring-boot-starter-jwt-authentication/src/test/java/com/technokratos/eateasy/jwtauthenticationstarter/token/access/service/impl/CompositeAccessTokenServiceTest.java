package com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompositeAccessTokenServiceTest {

    private static final String TEST_TOKEN = "test.token";
    private static final String USERNAME = "testUser";
    private static final Map<String, Object> EXTRA_CLAIMS = Collections.singletonMap("key", "value");
    private static final Map<String, Object> CLAIMS = Map.of("sub", USERNAME);

    @Mock(strictness = Mock.Strictness.LENIENT)
    private JwtAccessTokenGeneratorService generator;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private JwtAccessTokenParserService parser;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private UserDetails userDetails;

    @InjectMocks
    private CompositeAccessTokenService tokenService;


    @BeforeEach
    void setUp() {
        when(parser.extractUsername(TEST_TOKEN)).thenReturn(USERNAME);
        when(generator.generate(userDetails, EXTRA_CLAIMS)).thenReturn(TEST_TOKEN);
        doNothing().when(parser).validate(TEST_TOKEN);
        when(parser.extractAllClaims(TEST_TOKEN)).thenReturn(CLAIMS);
    }


    @Test
    void generateShouldDelegateToGeneratorService() {

        String token = tokenService.generate(userDetails, EXTRA_CLAIMS);

        assertEquals(TEST_TOKEN, token);
        verify(generator).generate(userDetails, EXTRA_CLAIMS);
    }

    @Test
    void extractUsernameShouldDelegateToParserService() {
        String result = tokenService.extractUsername(TEST_TOKEN);

        assertEquals(USERNAME, result);
        verify(parser).extractUsername(TEST_TOKEN);
    }

    @Test
    void validateShouldDelegateToParserService() {
        assertDoesNotThrow(() -> tokenService.validate(TEST_TOKEN));

        verify(parser).validate(TEST_TOKEN);
    }

    @Test
    void extractAllClaims_delegatesToParserService() {
        Map<String, Object> result = tokenService.extractAllClaims(TEST_TOKEN);

        assertEquals(CLAIMS, result);
        verify(parser).extractAllClaims(TEST_TOKEN);
    }
}