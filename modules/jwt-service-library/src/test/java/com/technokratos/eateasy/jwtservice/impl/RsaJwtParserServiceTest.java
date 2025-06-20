package com.technokratos.eateasy.jwtservice.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RsaJwtParserServiceTest {

    private static final String TEST_ISSUER = "test-issuer";
    private static final String TEST_SUBJECT = "test-user";
    private static final Duration TOKEN_EXPIRATION = Duration.ofHours(1);
    private static final String WRONG_ISSUER = "wrong-issuer";
    private static final String MALFORMED_TOKEN = "malformed.token.string";
    private static final String RSA = "RSA";

    private KeyPair keyPair;
    private RsaJwtParserService parser;
    private String validToken;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA);
        keyGen.initialize(2048);
        keyPair = keyGen.generateKeyPair();

        validToken = Jwts.builder()
                .subject(TEST_SUBJECT)
                .issuer(TEST_ISSUER)
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(TOKEN_EXPIRATION)))
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();



        parser = RsaJwtParserService.builder()
                .publicKey(keyPair.getPublic())
                .issuer(TEST_ISSUER)
                .build();
    }

    @Test
    void validateWithValidTokenShouldNotThrowException() {
        assertDoesNotThrow(() -> parser.validate(validToken));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validateWithNullOrBlankTokenShouldThrowBadCredentialsException(String token) {
        assertThrows(BadCredentialsException.class, () -> parser.validate(token));
    }

    @Test
    void validateWithExpiredTokenShouldThrowCredentialsExpiredException() {
        String expiredToken = createTokenWithExpiration(Duration.ofHours(-1));
        assertThrows(CredentialsExpiredException.class, () -> parser.validate(expiredToken));
    }

    @Test
    void validateWithTokenWithoutExpirationShouldThrowBadCredentialException() {

        String tokenWithoutExpiration = createTokenWithoutExpiration();

        assertThrows(BadCredentialsException.class, () -> parser.validate(tokenWithoutExpiration));
    }

    @Test
    void validateWithInvalidSignatureShouldThrowBadCredentialsException() throws Exception{
        KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA);
        generator.initialize(2048);
        KeyPair otherKeyPair = generator.generateKeyPair();

        String invalidSigToken = createTokenWithKey(otherKeyPair.getPrivate());

        assertThrows(BadCredentialsException.class, () -> parser.validate(invalidSigToken));
    }

    @Test
    void validateWithWrongIssuerShouldThrowBadCredentialsException() {
        String wrongIssuerToken = createTokenWithIssuer(WRONG_ISSUER);

        assertThrows(BadCredentialsException.class, () -> parser.validate(wrongIssuerToken));
    }

    @Test
    void validateWithMissingSubjectShouldThrowInsufficientAuthenticationException() {
        String noSubjectToken = createTokenWithoutSubject();

        assertThrows(InsufficientAuthenticationException.class, () -> parser.validate(noSubjectToken));
    }

    @Test
    void validateWithEmptySubjectShouldThrowInsufficientAuthenticationException() {
        String noSubjectToken = createTokenWithEmptySubject();

        assertThrows(InsufficientAuthenticationException.class, () -> parser.validate(noSubjectToken));
    }

    @Test
    void validateWithNotSignedTokenShouldThrowBadCredentialsException() {
        String notSignedToken = createNotSignedToken();

        assertThrows(BadCredentialsException.class, () -> parser.validate(notSignedToken));
    }

    @Test
    void validateWithTokenWithNotBeforeShouldThrowBadCredentialException() {
        String token = createTokenWithNotBefore();

        assertThrows(BadCredentialsException.class, () -> parser.validate(token));
    }

    @Test
    void validateWithMalformedTokenShouldThrowBadCredentialsException() {
        assertThrows(BadCredentialsException.class, () -> parser.validate(MALFORMED_TOKEN));
    }
    @Test
    void extractAllClaimsWithValidTokenShouldReturnClaimsException() {
        Claims claims = parser.extractAllClaims(validToken);

        assertAll(
                () -> assertEquals(TEST_SUBJECT, claims.getSubject()),
                () -> assertEquals(TEST_ISSUER, claims.getIssuer())
        );
    }

    @Test
    void extractAllClaimsWithInvalidTokenShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> parser.extractAllClaims(MALFORMED_TOKEN));
    }

    @Test
    void extractUsernameWithValidTokenShouldReturnSubject() {
        String username = parser.extractUsername(validToken);
        assertNotNull(username);
        assertEquals(TEST_SUBJECT, username);
    }

    @Test
    void extractClaimWithValidTokenShouldReturnClaimValue() {
        Date expiration = parser.extractClaim(validToken, Claims::getExpiration);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    private String createTokenWithoutSubject() {
        return Jwts.builder()
                .issuer(TEST_ISSUER)
                .expiration(Date.from(Instant.now().plus(TOKEN_EXPIRATION)))
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    private String createTokenWithEmptySubject() {
        return Jwts.builder()
                .subject("\u000B\r\n")
                .issuer(TEST_ISSUER)
                .expiration(Date.from(Instant.now().plus(TOKEN_EXPIRATION)))
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    private String createTokenWithExpiration(Duration duration) {
        return Jwts.builder()
                .subject(TEST_SUBJECT)
                .issuer(TEST_ISSUER)
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(duration)))
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    private String createTokenWithIssuer(String issuer) {
        return Jwts.builder()
                .subject(TEST_SUBJECT)
                .issuer(issuer)
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    private String createTokenWithKey(PrivateKey key) {
        return Jwts.builder()
                .subject(TEST_SUBJECT)
                .issuer(TEST_ISSUER)
                .signWith(key, Jwts.SIG.RS256)
                .compact();
    }

    private String createNotSignedToken() {
       return Jwts.builder()
               .subject(TEST_SUBJECT)
               .issuer(TEST_ISSUER)
               .issuedAt(new Date())
               .expiration(Date.from(Instant.now().plus(TOKEN_EXPIRATION)))
               .compact();
    }

    private String createTokenWithoutExpiration() {
        return Jwts.builder()
                .subject(TEST_SUBJECT)
                .issuer(TEST_ISSUER)
                .issuedAt(new Date())
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    private String createTokenWithNotBefore() {
        return Jwts.builder()
                .issuer(TEST_ISSUER)
                .subject(TEST_SUBJECT)
                .expiration(Date.from(Instant.now().plusSeconds(7200)))
                .notBefore(Date.from(Instant.now().plus(Duration.ofHours(1))))
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }
}