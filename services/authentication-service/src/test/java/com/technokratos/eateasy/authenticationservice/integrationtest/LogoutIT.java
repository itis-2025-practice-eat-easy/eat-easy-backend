package com.technokratos.eateasy.authenticationservice.integrationtest;

import com.redis.testcontainers.RedisContainer;
import com.technokratos.eateasy.jwtauthenticationstarter.dto.request.RefreshRequest;
import com.technokratos.eateasy.jwtauthenticationstarter.dto.response.TokenResponse;
import com.technokratos.eateasy.jwtauthenticationstarter.properties.JwtProperties;
import com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.model.RefreshTokenEntity;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenService;
import com.technokratos.eateasy.jwtservice.JwtGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LogoutIT {

    private static final String REDIS_IMAGE = "redis:8.0.1-alpine";
    private static final String FINGERPRINT = "fingerprint";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String ROLE = "USER";
    private static final UUID REFRESH_TOKEN_ID = UUID.randomUUID();
    private static final String LOGOUT_URL = "/api/v1/auth/logout";
    private static final HttpStatusCode SC_OK = HttpStatusCode.valueOf(200);
    private static final UserDetails USER = User.builder()
            .username(LOGIN)
            .password((new BCryptPasswordEncoder().encode(PASSWORD)))
            .roles(ROLE)
            .build();
    private static final RefreshTokenEntity TOKEN_ENTITY = RefreshTokenEntity.builder()
            .fingerprint((new BCryptPasswordEncoder()).encode(FINGERPRINT))
            .username(LOGIN)
            .id(REFRESH_TOKEN_ID)
            .expiresAt(Instant.now().plus(Duration.ofMinutes(10)))
            .createdAt(Instant.now())
            .build();


    @Container
    @ServiceConnection
    private static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse(REDIS_IMAGE));

    private @Autowired TestRestTemplate restTemplate;
    private @Autowired RefreshTokenService refreshTokenService;
    private @Autowired RedisTemplate<String, RefreshTokenEntity> redisTemplate;
    @Value("${custom.repository.refresh-token.key-prefix}")
    private String refreshTokenKeyPrefix;
    private @MockitoBean UserDetailsService userDetailsService;
    @Token(Token.TokenType.REFRESH)
    private @Autowired JwtGeneratorService refreshTokenGeneratorService;
    private @Autowired JwtProperties jwtProperties;


    private String token;
    private String refreshTokenIdClaim;

    @BeforeEach
    void setUp() {
        refreshTokenIdClaim = jwtProperties.getTokens().getRefresh().getClaims().getRefreshTokenId();
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().serverCommands().flushDb();
        when(userDetailsService.loadUserByUsername(eq(LOGIN))).thenReturn(USER);
        prepareRefreshToken();
    }

    private void prepareRefreshToken() {
        token = refreshTokenGeneratorService.generate(USER, Map.of(refreshTokenIdClaim, REFRESH_TOKEN_ID));
        redisTemplate.opsForValue().set(refreshTokenKeyPrefix + REFRESH_TOKEN_ID, TOKEN_ENTITY);
    }

    @Test
    void logoutWithGoodCredentialShouldReturnOK() {
        ResponseEntity<TokenResponse> response =  restTemplate.exchange(
                LOGOUT_URL, HttpMethod.POST,
                new HttpEntity<>(new RefreshRequest(FINGERPRINT, token)),
                TokenResponse.class);

        assertEquals(SC_OK, response.getStatusCode());
    }


    @Test
    void logoutWithBadCredentialShouldReturnOKAndDoNothing() {
        ResponseEntity<TokenResponse> response =  restTemplate.exchange(
                LOGOUT_URL, HttpMethod.POST,
                new HttpEntity<>(new RefreshRequest(FINGERPRINT, "bad-token")),
                TokenResponse.class);

        assertEquals(SC_OK, response.getStatusCode());
    }
}
