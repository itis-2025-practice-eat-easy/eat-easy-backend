package com.technokratos.eateasy.common.dto.response;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {
    private static final String PATH = "/api/v1/users";
    private static final String ERROR_MSG = "Validation failed";
    private static final Object DETAILS = new Object();

    @Test
    void buildSuccessWithAllFields() {
        ErrorResponse response = ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .path(PATH)
                .error(ERROR_MSG)
                .details(DETAILS)
                .build();

        assertNotNull(response.getTimestamp());
        assertEquals(400, response.getHttpStatus());
        assertEquals(PATH, response.getPath());
        assertEquals(ERROR_MSG, response.getError());
        assertEquals(DETAILS, response.getDetails());
    }

    @Test
    void buildSuccessWithRequiredFieldsOnly() {
        ErrorResponse response = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .path(PATH)
                .error(ERROR_MSG)
                .build();

        assertEquals(404, response.getHttpStatus());
        assertNull(response.getDetails());
    }

    @Test
    void buildFailsWhenMissingHttpStatus() {
        assertThrows(IllegalStateException.class, () ->
                ErrorResponse.builder()
                        .path(PATH)
                        .error(ERROR_MSG)
                        .build()
        );
    }

    @Test
    void buildFailsWhenMissingPath() {
        assertThrows(IllegalStateException.class, () ->
                ErrorResponse.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .error(ERROR_MSG)
                        .build()
        );
    }

    @Test
    void buildFailsWhenMissingError() {
        assertThrows(IllegalStateException.class, () ->
                ErrorResponse.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .path(PATH)
                        .build()
        );
    }

    @Test
    void timestampIsRecent() {
        ZonedDateTime before = ZonedDateTime.now().minusSeconds(1);
        ErrorResponse response = ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .path(PATH)
                .error(ERROR_MSG)
                .build();

        assertTrue(response.getTimestamp().isAfter(before));
        assertTrue(response.getTimestamp().isBefore(ZonedDateTime.now().plusSeconds(1)));
    }
}