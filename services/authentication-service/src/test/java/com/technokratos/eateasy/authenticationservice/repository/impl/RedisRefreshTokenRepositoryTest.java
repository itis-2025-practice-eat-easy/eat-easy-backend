package com.technokratos.eateasy.authenticationservice.repository.impl;

import com.technokratos.eateasy.authenticationservice.util.RefreshTokenRedisKeyUtil;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.model.RefreshTokenEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisRefreshTokenRepositoryTest {

    private static final UUID TEST_ID = UUID.randomUUID();
    private static final String TEST_KEY = "test_key:" + TEST_ID;
    private static final Duration TEST_DURATION = Duration.ofHours(1);


    private @Mock RedisTemplate<String, RefreshTokenEntity> redisTemplate;
    private @Mock RefreshTokenRedisKeyUtil util;
    private @Mock ValueOperations<String, RefreshTokenEntity> valueOperations;
    private @Mock RefreshTokenEntity refreshToken;

    @InjectMocks
    private RedisRefreshTokenRepository repository;

    @BeforeEach
    void setUp() {
        lenient().when(util.generateId()).thenReturn(TEST_ID);
        lenient().when(util.getKey(TEST_ID)).thenReturn(TEST_KEY);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        lenient().when(refreshToken.getExpiresAt()).thenReturn(Instant.now().plus(TEST_DURATION));

    }

    @Test
    void saveWithPositiveExpirationTimeShouldSaveToRedis() {
        UUID result = repository.save(refreshToken);

        assertEquals(TEST_ID, result);

        verify(util).generateId();
        verify(refreshToken).setId(TEST_ID);
        verify(refreshToken).setCreatedAt(argThat(created -> {
            Duration duration =  Duration.between(created, Instant.now());
            return duration.isPositive() && duration.toMillis() < 1000;
        }));
        verify(util).getKey(TEST_ID);
        verify(valueOperations).set(same(TEST_KEY), same(refreshToken), argThat(ttl ->
                !TEST_DURATION.minus(ttl).isNegative() && TEST_DURATION.minus(ttl).toMillis() < 1000));
    }

    @Test
    void saveWithNegativeExpirationTimeShouldNotSaveToRedis() {
        when(refreshToken.getExpiresAt()).thenReturn(Instant.now().minus(TEST_DURATION));

        UUID result = repository.save(refreshToken);

        assertEquals(TEST_ID, result);

        verify(util).generateId();
        verify(refreshToken).setId(TEST_ID);
        verify(refreshToken).setCreatedAt(argThat(created -> {
            Duration duration =  Duration.between(created, Instant.now());
            return duration.isPositive() && duration.toMillis() < 1000;
        }));

        verify(util, never()).getKey(any());
        verifyNoInteractions(valueOperations, redisTemplate);
    }

    @Test
    void findByIdShouldReturnValue() {
        when(valueOperations.get(TEST_KEY)).thenReturn(refreshToken);

        Optional<RefreshTokenEntity> result = repository.findById(TEST_ID);

        assertTrue(result.isPresent());
        assertEquals(refreshToken, result.get());

        verify(redisTemplate).opsForValue();
        verify(valueOperations).get(TEST_KEY);
    }

    @Test
    void deleteByIdShouldDeleteValue() {
        repository.deleteById(TEST_ID);

        verify(redisTemplate).delete(TEST_KEY);
    }

    @Test
    void existByIdShouldReturnTrue() {
        when(redisTemplate.hasKey(TEST_KEY)).thenReturn(true);

        boolean result = repository.existById(TEST_ID);

        assertTrue(result);

        verify(redisTemplate).hasKey(TEST_KEY);
    }
}