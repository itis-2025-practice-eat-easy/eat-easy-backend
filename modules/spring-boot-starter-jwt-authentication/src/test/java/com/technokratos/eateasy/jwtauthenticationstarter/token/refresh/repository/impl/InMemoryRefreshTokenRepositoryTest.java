package com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.repository.impl;

import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.model.RefreshTokenEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryRefreshTokenRepositoryTest {

    private InMemoryRefreshTokenRepository repository;
    private RefreshTokenEntity testEntity;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRefreshTokenRepository();
        testEntity = RefreshTokenEntity.builder()
                .fingerprint("fingerprint")
                .expiresAt(Instant.now().plusSeconds(3600))
                .createdAt(Instant.now())
                .username("user")
                .build();
    }

    @Test
    void saveShouldGenerateNewIdAndStoresEntity() {
        UUID id = repository.save(testEntity);
        assertNotNull(id);
        assertEquals(id, testEntity.getId());

        Optional<RefreshTokenEntity> found = repository.findById(id);
        assertTrue(found.isPresent());
        assertEquals(testEntity, found.get());
    }

    @Test
    void findByIdShouldReturnEmptyForNonExistentId() {
        UUID randomId = UUID.randomUUID();
        Optional<RefreshTokenEntity> result = repository.findById(randomId);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteByIdShouldRemoveExistingEntity() {
        UUID id = repository.save(testEntity);
        repository.deleteById(id);
        assertFalse(repository.existById(id));
    }

    @Test
    void deleteByIdShouldIgnoreNonExistentId() {
        UUID randomId = UUID.randomUUID();
        assertDoesNotThrow(() -> repository.deleteById(randomId));
    }

    @Test
    void existByIdShouldReturnTrueForExistingEntity() {
        UUID id = repository.save(testEntity);
        assertTrue(repository.existById(id));
    }

    @Test
    void existByIdShouldReturnFalseForNonExistentEntity() {
        assertFalse(repository.existById(UUID.randomUUID()));
    }
}