package com.technokratos.eateasy.jwtservice.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RsaJwtGeneratorServiceTest {

    private static final String TEST_USER = "testUser";
    private static final String TEST_ISSUER = "test-issuer";
    private static final Duration TEST_DURATION = Duration.ofHours(1);

    private KeyPair keyPair;
    private UserDetails userDetails;
    private RsaJwtGeneratorService generator;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        keyPair = keyGen.generateKeyPair();

        userDetails = User.withUsername(TEST_USER)
                .password("password")
                .authorities("ROLE_USER")
                .build();

        generator = new RsaJwtGeneratorService(keyPair.getPrivate(), TEST_DURATION, TEST_ISSUER);
    }

    @Test
    void generateWithValidUserAndClaimsShouldReturnToken() {
        Map<String, Object> claims = new HashMap<>();
        String claimKey = "customClaim";
        String claimValue = "value";
        claims.put(claimKey, claimValue);

        String token = generator.generate(userDetails, claims);

        assertNotNull(token);
        verifyTokenStructure(token);
        assertEquals(claimValue, extractClaim(token, claimKey));
    }

    @Test
    void generateWithExtraClaimsWhenNotSupportedShouldThrowsException() {
        generator = RsaJwtGeneratorService.builder()
                .privateKey(keyPair.getPrivate())
                .expiration(TEST_DURATION)
                .issuer(TEST_ISSUER)
                .supportsExtraClaims(false)
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> generator.generate(userDetails, Map.of("claim", "value")));
    }

    @Test
    void generateWithEmptyClaimsWhenSupportedShouldReturnToken() {
        generator = RsaJwtGeneratorService.builder()
                .privateKey(keyPair.getPrivate())
                .expiration(TEST_DURATION)
                .issuer(TEST_ISSUER)
                .build();

        String token = generator.generate(userDetails, Collections.emptyMap());

        assertNotNull(token);
        verifyTokenStructure(token);
    }

    @Test
    void generateWithEmptyClaimsWhenNotSupportedShouldReturnToken() {
        generator = RsaJwtGeneratorService.builder()
                .privateKey(keyPair.getPrivate())
                .expiration(TEST_DURATION)
                .issuer(TEST_ISSUER)
                .supportsExtraClaims(false)
                .build();

        String token = generator.generate(userDetails, Collections.emptyMap());

        assertNotNull(token);
        verifyTokenStructure(token);
    }

    @Test
    void generatedTokenContainsCorrectSubjectIssuerAndExpiration() {
        String token = generator.generate(userDetails, Collections.emptyMap());
        Claims claims = parseToken(token).getPayload();

        assertAll(
                () -> assertEquals(TEST_USER, claims.getSubject()),
                () -> assertEquals(TEST_ISSUER, claims.getIssuer()),
                () -> assertTrue(claims.getExpiration().after(new Date()))
        );
    }

    @Test
    void generatedTokenUsesRS256Algorithm() {
        String token = generator.generate(userDetails, Collections.emptyMap());
        Jws<Claims> jws = parseToken(token);

        assertEquals("RS256", jws.getHeader().getAlgorithm());
    }

    private Jws<Claims> parseToken(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build();
        return parser.parseSignedClaims(token);
    }

    private Object extractClaim(String token, String claimKey) {
        return parseToken(token).getPayload().get(claimKey);
    }

    private void verifyTokenStructure(String token) {
        assertAll(
                () -> assertEquals(3, token.split("\\.").length),
                () -> assertDoesNotThrow(() -> parseToken(token))
        );
    }
}