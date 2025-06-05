package com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UuidClaimExtractorTest {
    private static final String CLAIM_NAME = "uuid_claim";
    private static final UUID TEST_UUID = UUID.randomUUID();
    private static final String VALID_UUID_STRING = TEST_UUID.toString();
    private static final String INVALID_UUID_STRING = "not-a-uuid";
    private static final Random RANDOM = new Random();
    private UuidClaimExtractor extractor;

    @BeforeEach
    void setUp() {
        if (RANDOM.nextBoolean()) {
            extractor = new UuidClaimExtractor(CLAIM_NAME);
        } else {
            extractor = new UuidClaimExtractor();
            extractor.claimName(CLAIM_NAME);
        }
    }

    @Test
    void extractValidUuidObjectShouldReturnUuid() {
        Map<String, Object> claims = Map.of(CLAIM_NAME, TEST_UUID);

        UUID result = extractor.extract(claims);

        assertEquals(TEST_UUID, result);
    }

    @Test
    void extractValidUuidStringShouldReturnParsedUuid() {
        Map<String, Object> claims = Map.of(CLAIM_NAME, VALID_UUID_STRING);

        UUID result = extractor.extract(claims);

        assertEquals(TEST_UUID, result);
    }

    @Test
    void extractInvalidUuidStringShouldReturnNull() {
        Map<String, Object> claims = Map.of(CLAIM_NAME, INVALID_UUID_STRING);

        UUID result = extractor.extract(claims);

        assertNull(result);
    }

    @Test
    void extractNonUuidTypeShouldReturnNull() {
        extractor.claimName(CLAIM_NAME);
        Map<String, Object> claims = Map.of(CLAIM_NAME, 12345);

        UUID result = extractor.extract(claims);

        assertNull(result);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void extractMissingClaimShouldReturnNull(String claimValue) {
        extractor.claimName(CLAIM_NAME);
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_NAME, claimValue);

        UUID result = extractor.extract(claims);

        assertNull(result);
    }

    @Test
    void extractNotConfiguredClaimNameShouldThrowIllegalStateException() {
        extractor.claimName(null);
        Map<String, Object> claims = Map.of(CLAIM_NAME, TEST_UUID);

        assertThrows(IllegalStateException.class,
                () -> extractor.extract(claims));
    }
}