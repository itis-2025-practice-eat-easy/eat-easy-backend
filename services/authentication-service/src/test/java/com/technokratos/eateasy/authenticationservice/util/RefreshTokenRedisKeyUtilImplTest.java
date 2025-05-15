package com.technokratos.eateasy.authenticationservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenRedisKeyUtilImplTest {

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    private RefreshTokenRedisKeyUtilImpl keyUtil;

    @BeforeEach
    void setUp() {
        this.keyUtil = new RefreshTokenRedisKeyUtilImpl(REFRESH_TOKEN_PREFIX);
    }

    @Test
    void generateIdShouldReturnNotNullUuid() {
        UUID id = keyUtil.generateId();
        assertNotNull(id);
    }

    @Test
    void getKeyShouldReturnKeyWithPrefix() {
        UUID id = UUID.randomUUID();
        String expectedKey = REFRESH_TOKEN_PREFIX + id;

        String actualKey = keyUtil.getKey(id);

        assertEquals(expectedKey, actualKey);
    }
}