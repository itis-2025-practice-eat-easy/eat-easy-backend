package com.technokratos.eateasy.authenticationservice.repository.impl;

import com.technokratos.eateasy.authenticationservice.util.RefreshTokenRedisKeyUtil;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.model.RefreshTokenEntity;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    private final RedisTemplate<String, RefreshTokenEntity> redisTemplate;
    private final RefreshTokenRedisKeyUtil util;

    @Override
    public UUID save(RefreshTokenEntity refreshTokenEntity) {
        UUID id = util.generateId();
        refreshTokenEntity.setId(id);
        refreshTokenEntity.setCreatedAt(Instant.now());

        Duration expirationTime = calculateExpirationTime(refreshTokenEntity);

        if (!refreshTokenEntity.getExpiresAt().isAfter(Instant.now())) {
            log.warn("Skipping save of refresh token with expired TTL: id={}", id);
            return id;
        }

        redisTemplate.opsForValue().set(util.getKey(id), refreshTokenEntity, expirationTime);
        log.debug("Saved refresh token with id={} and expiration time={}", id, expirationTime);
        return id;
    }

    private Duration calculateExpirationTime(RefreshTokenEntity refreshTokenEntity) {
        Duration expirationTime = Duration.between(Instant.now(), refreshTokenEntity.getExpiresAt());
        log.trace("Calculated expiration time for refresh token with id={}: {}", refreshTokenEntity.getId(), expirationTime);
        return expirationTime;
    }

    @Override
    public Optional<RefreshTokenEntity> findById(UUID id) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(util.getKey(id)));
    }

    @Override
    public void deleteById(UUID id) {
        redisTemplate.delete(util.getKey(id));
    }

    @Override
    public boolean existById(UUID id) {
        Boolean hasKey = redisTemplate.hasKey(util.getKey(id));
        return Boolean.TRUE.equals(hasKey);
    }
}
