package com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.impl;

import com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token;
import com.technokratos.eateasy.jwtauthenticationstarter.security.userdetails.IdentifiableUserDetails;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.model.RefreshTokenEntity;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.repository.RefreshTokenRepository;
import com.technokratos.eateasy.jwtservice.JwtGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token.TokenType.REFRESH;
import static com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.impl.RefreshTokenServiceConstants.REFRESH_TOKEN_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtRefreshTokenGeneratorServiceTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String FINGERPRINT = "fingerprint";
    private static final String FINGERPRINT_HASH = "hashed-fingerprint";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final Duration EXPIRATION = Duration.ofHours(1);
    private static final UUID REFRESH_TOKEN_ENTITY_ID = UUID.randomUUID();
    private static final String TOKEN = "refresh.token";

    @Mock(strictness = Mock.Strictness.LENIENT)
    private RefreshTokenRepository repository;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private PasswordEncoder passwordEncoder;

    @Mock
    @Token(REFRESH)
    private JwtGeneratorService jwtGenerator;

    private JwtRefreshTokenGeneratorService tokenGenerator;

    private UserDetails userDetails;
    private IdentifiableUserDetails<UUID> identifiableUserDetails;

    @BeforeEach
    void setUp() {
        userDetails = User.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();

        identifiableUserDetails = new TestIdentifiableUserDetails(USER_ID, USERNAME, PASSWORD);

        tokenGenerator = JwtRefreshTokenGeneratorService.builder()
                .repository(repository)
                .passwordEncoder(passwordEncoder)
                .jwtGenerator(jwtGenerator)
                .expiration(EXPIRATION)
                .build();

        when(repository.save(any())).thenReturn(REFRESH_TOKEN_ENTITY_ID);
        when(repository.findById(any())).thenReturn(java.util.Optional.of(new RefreshTokenEntity()));
        when(repository.existById(any())).thenReturn(true);

        when(passwordEncoder.encode(FINGERPRINT)).thenReturn(FINGERPRINT_HASH);
        when(repository.save(any())).thenReturn(REFRESH_TOKEN_ENTITY_ID);
        when(jwtGenerator.generate(any(), any())).thenReturn(TOKEN);
    }

    @Test
    void generateShouldReturnToken() {
        String token = tokenGenerator.generate(userDetails, FINGERPRINT);

        assertEquals(TOKEN, token);
    }

    @Test
    void generateShouldPersistTokenWithHashedFingerprint() {

        tokenGenerator.generate(userDetails, FINGERPRINT);

        ArgumentCaptor<RefreshTokenEntity> entityCaptor = ArgumentCaptor.forClass(RefreshTokenEntity.class);
        verify(repository).save(entityCaptor.capture());

        RefreshTokenEntity entity = entityCaptor.getValue();
        assertAll(
                () -> assertEquals(FINGERPRINT_HASH, entity.getFingerprint()),
                () -> assertEquals(USERNAME, entity.getUsername()),
                () -> assertTrue(entity.getExpiresAt().isAfter(Instant.now()))
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void generateWithIdentifiableUserDetailsShouldIncludeTokenIdAndUserIdInClaims() {
        ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
        tokenGenerator.generate(identifiableUserDetails, FINGERPRINT);

        verify(jwtGenerator).generate(eq(identifiableUserDetails), claimsCaptor.capture());
        Map<String, Object> claims = claimsCaptor.getValue();

        assertAll(
                () -> assertEquals(REFRESH_TOKEN_ENTITY_ID, claims.get(REFRESH_TOKEN_ID)),
                () -> assertEquals(USER_ID, claims.get(RefreshTokenServiceConstants.USER_ID))
        );
    }

    @Test
    void generateShouldHandleNullFingerprint() {
        assertDoesNotThrow(() -> tokenGenerator.generate(userDetails, null));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void generateShouldSkipUserIdForNonIdentifiableUser() {
        ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
        tokenGenerator.generate(userDetails, FINGERPRINT);

        verify(jwtGenerator).generate(any(), claimsCaptor.capture());
        assertFalse(claimsCaptor.getValue().containsKey(RefreshTokenServiceConstants.USER_ID));
    }

    private static class TestIdentifiableUserDetails implements IdentifiableUserDetails<UUID> {
        private final UUID id;
        private final UserDetails delegate;

        TestIdentifiableUserDetails(UUID id, String username, String password) {
            this.id = id;
            this.delegate = User.builder()
                    .username(username)
                    .password(password)
                    .build();
        }

        @Override public UUID getId() { return id; }
        @Override public String getUsername() { return delegate.getUsername(); }
        @Override public String getPassword() { return delegate.getPassword(); }
        @Override public Collection<? extends GrantedAuthority> getAuthorities() { return delegate.getAuthorities(); }
    }
}