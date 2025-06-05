package com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.repository;

import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.model.RefreshTokenEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository contract for refresh token persistence operations.
 * <p>
 * Manages token storage lifecycle including creation, retrieval, and deletion.
 * </p>
 */
public interface RefreshTokenRepository {
    /**
     * Persists refresh token entity and returns generated ID
     * @param refreshTokenEntity token entity to save
     * @return generated unique identifier
     */
    UUID save(RefreshTokenEntity refreshTokenEntity);

    /**
     * Retrieves token entity by ID
     * @param id token identifier
     * @return Optional containing found entity or empty
     */
    Optional<RefreshTokenEntity> findById(UUID id);

    /**
     * Removes token entity by ID
     * @param id token identifier to delete
     */
    void deleteById(UUID id);

    /**
     * Checks token existence by ID
     * @param id token identifier to check
     * @return true if entity exists, false otherwise
     */
    boolean existById(UUID id);
}
