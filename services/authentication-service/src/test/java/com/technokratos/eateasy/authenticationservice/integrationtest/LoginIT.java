package com.technokratos.eateasy.authenticationservice.integrationtest;

import com.redis.testcontainers.RedisContainer;
import com.technokratos.eateasy.jwtauthenticationstarter.dto.request.LoginRequest;
import com.technokratos.eateasy.jwtauthenticationstarter.dto.response.TokenResponse;
import com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.AccessTokenParserService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.model.RefreshTokenEntity;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginIT {

    private static final String REDIS_IMAGE = "redis:8.0.1-alpine";
    private static final String FINGERPRINT = "fingerprint";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String ROLE = "USER";
    private static final String LOGIN_URL = "/api/v1/auth/login";

    @Container
    @ServiceConnection
    private static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse(REDIS_IMAGE));

    private @Autowired TestRestTemplate restTemplate;
    private @Autowired PasswordEncoder passwordEncoder;
    private @Autowired RefreshTokenService refreshTokenService;
    private @Autowired AccessTokenParserService accessTokenService;
    private @Autowired RedisTemplate<String, RefreshTokenEntity> redisTemplate;
    @Value("${custom.repository.refresh-token.key-prefix}")
    private String refreshTokenKeyPrefix;
    private @MockitoBean UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().serverCommands().flushDb();

        when(userDetailsService.loadUserByUsername(eq(LOGIN))).thenReturn(User.builder()
                .username(LOGIN)
                .password(passwordEncoder.encode(PASSWORD))
                .roles(ROLE)
                .build());
    }

    @Test
    void loginWithGoodCredentialsShouldReturnTokens() {
        ResponseEntity<TokenResponse> response =  restTemplate.exchange(
                LOGIN_URL, HttpMethod.POST,
                new HttpEntity<>(new LoginRequest(FINGERPRINT, LOGIN, PASSWORD)),
                TokenResponse.class);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(response.getBody());

        String access = response.getBody().access();
        String refresh = response.getBody().refresh();
        assertNotNull(access);
        assertNotNull(refresh);
    }

    @Test
    void loginWithGoodCredentialsShouldSaveRefreshToken() {
        ResponseEntity<TokenResponse> response =  restTemplate.exchange(
                LOGIN_URL, HttpMethod.POST,
                new HttpEntity<>(new LoginRequest(FINGERPRINT, LOGIN, PASSWORD)),
                TokenResponse.class);

        String access = response.getBody().access();
        String refresh = response.getBody().refresh();

        assertDoesNotThrow(() -> accessTokenService.validate(access));
        assertDoesNotThrow(() -> refreshTokenService.validate(refresh, FINGERPRINT));

        verifyThatRefreshTokenIsSaved(refresh);
    }

    private void verifyThatRefreshTokenIsSaved(String refresh) {
        UUID refreshTokenId = refreshTokenService.extractId(refresh);
        assertNotNull(refreshTokenId);

        RefreshTokenEntity tokenEntity = redisTemplate.opsForValue().get(refreshTokenKeyPrefix + refreshTokenId);
        assertNotNull(tokenEntity);
        assertEquals(LOGIN, tokenEntity.getUsername());
        assertTrue(passwordEncoder.matches(FINGERPRINT, tokenEntity.getFingerprint()));
    }

    @Test
    void loginWithBadCredentialShouldReturnUnauthorized() {
        ResponseEntity<Map<String, Object>> response =  restTemplate.exchange(
                LOGIN_URL, HttpMethod.POST,
                new HttpEntity<>(new LoginRequest(FINGERPRINT, LOGIN, "wrongPassword")),
                new ParameterizedTypeReference<Map<String, Object>>() {});

        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid username or password", response.getBody().get("error"));

        verifyThatRefreshTokenIsNotSaved();
    }

    private void verifyThatRefreshTokenIsNotSaved() {
        Set<String> keys = redisTemplate.keys(allRefreshTokenKeys());
        assertTrue(keys == null || keys.isEmpty());
    }

    private String allRefreshTokenKeys() {
        return refreshTokenKeyPrefix + "*";
    }
}
