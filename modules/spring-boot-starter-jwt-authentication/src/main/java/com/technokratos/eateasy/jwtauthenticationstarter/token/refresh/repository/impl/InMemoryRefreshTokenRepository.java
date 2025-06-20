package com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.repository.impl;

import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.model.RefreshTokenEntity;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory thread-safe implementation of refresh token repository.
 * <p>
 * Uses {@link ConcurrentHashMap} for storage with UUID keys. Suitable for:
 * <ul>
 *   <li>Testing environments</li>
 *   <li>Single-node development setups</li>
 *   <li>Prototyping phases</li>
 * </ul>
 *
 * @implNote This implementation is not suitable for production use due to lack of persistence and scalability.
 *
 * @see RefreshTokenRepository
 */
@Slf4j
public class InMemoryRefreshTokenRepository implements RefreshTokenRepository {

    private final Map<UUID, RefreshTokenEntity> store = new ConcurrentHashMap<>();

    @Override
    public UUID save(RefreshTokenEntity refreshTokenEntity) {
        UUID id = UUID.randomUUID();
        refreshTokenEntity.setId(id);
        refreshTokenEntity.setCreatedAt(Instant.now());
        store.put(id, refreshTokenEntity);
        log.debug("Saved refresh token with id: {}", id);
        return id;
    }

    @Override
    public Optional<RefreshTokenEntity> findById(UUID id) {
        log.trace("Finding refresh token with id: {}", id);

        RefreshTokenEntity entity = store.get(id);
        if (entity != null) {
            log.debug("Found refresh token with id: {}", id);
            return Optional.of(entity);
        } else {
            log.debug("Refresh token with id: {} not found", id);
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(UUID id) {
        RefreshTokenEntity entity = store.remove(id);
        if (entity != null) {
            log.debug("Deleted refresh token with id: {}", id);
        } else {
            log.debug("Refresh token with id: {} not found for deletion", id);
        }
    }

    @Override
    public boolean existById(UUID id) {
        boolean exist = store.containsKey(id);
        if (exist) {
            log.debug("Refresh token with id: {} exists", id);
        } else {
            log.debug("Refresh token with id: {} does not exist", id);
        }
        return exist;
    }
}
