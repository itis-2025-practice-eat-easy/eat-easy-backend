package com.technokratos.eateasy.jwtservice.impl;

import com.technokratos.eateasy.jwtservice.JwtGeneratorService;
import com.technokratos.eateasy.jwtservice.JwtParserService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompositeJwtServiceTest {

    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_ROLE = "ROLE_USER";
    private static final String GENERATED_TOKEN = "generatedToken";
    private static final String TEST_TOKEN = "testToken";
    private static final String INVALID_TOKEN = "invalidToke";

    @Mock(strictness = Mock.Strictness.LENIENT)
    private JwtGeneratorService generatorService;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private JwtParserService parserService;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private Claims claims;

    @InjectMocks
    private CompositeJwtService jwtService;

    private final UserDetails testUser = User.builder()
            .username(TEST_USERNAME)
            .password(TEST_PASSWORD)
            .authorities(TEST_ROLE)
            .build();

    @BeforeEach
    void init() {
        when(generatorService.generate(eq(testUser), anyMap())).thenReturn(GENERATED_TOKEN);
        doNothing().when(parserService).validate(eq(TEST_TOKEN));
        doThrow(new BadCredentialsException("Invalid token")).when(parserService).validate(eq(INVALID_TOKEN));
        when(parserService.extractAllClaims(eq(TEST_TOKEN))).thenReturn(claims);
        when(parserService.extractUsername(eq(TEST_TOKEN))).thenReturn(TEST_USERNAME);
        when(parserService.extractClaim(eq(TEST_TOKEN), any())).thenReturn(TEST_USERNAME);
    }

    @Test
    void generateShouldDelegateToGeneratorService() {
        Map<String, Object> claims = Collections.singletonMap("key", "value");

        String result = jwtService.generate(testUser, claims);

        assertEquals(GENERATED_TOKEN, result);
        verify(generatorService).generate(testUser, claims);
    }

    @Test
    void validateShouldDelegateToParserService() throws AuthenticationException {
        doNothing().when(parserService).validate(TEST_TOKEN);

        assertDoesNotThrow(() -> jwtService.validate(TEST_TOKEN));

        verify(parserService).validate(TEST_TOKEN);
    }

    @Test
    void validateShouldPropagateAuthenticationException() {
        doThrow(new BadCredentialsException("Invalid")).when(parserService).validate(INVALID_TOKEN);

        assertThrows(BadCredentialsException.class, () -> jwtService.validate(INVALID_TOKEN));
    }

    @Test
    void extractAllClaimsShouldDelegatesToParserService() {

        Claims result = jwtService.extractAllClaims(TEST_TOKEN);

        assertSame(claims, result);
        verify(parserService).extractAllClaims(TEST_TOKEN);
    }

    @Test
    void extractUsernameDelegatesToParserService() {

        String result = jwtService.extractUsername(TEST_TOKEN);

        assertEquals(TEST_USERNAME, result);
        verify(parserService).extractUsername(TEST_TOKEN);
    }

    @Test
    void extractClaim_delegatesToParserService() {
        Function<Claims, String> claimExtractor = Claims::getSubject;

        String result = jwtService.extractClaim(TEST_TOKEN, claimExtractor);

        assertEquals(TEST_USERNAME, result);
        verify(parserService).extractClaim(TEST_TOKEN, claimExtractor);
    }
}