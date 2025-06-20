package com.technokratos.eateasy.common.exceptionhandler;

import com.technokratos.eateasy.common.dto.response.ErrorResponse;
import com.technokratos.eateasy.common.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorResponseServiceExceptionHandlerTest {

    private static final String PATH = "path";
    private static final String MESSAGE = "message";
    private static final Object DETAILS = new Object();

    @Mock
    private HttpServletRequest request;

    private ErrorResponseServiceExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ErrorResponseServiceExceptionHandler();
        when(request.getRequestURI()).thenReturn(PATH);
    }

    @Test
    void handleClientErrorServiceException() {
        ClientErrorServiceException ex = new NotFoundServiceException(MESSAGE, DETAILS);


        ResponseEntity<ErrorResponse> response = handler.handleClientErrorServiceException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.NOT_FOUND.value(), body.getHttpStatus());
        assertEquals(PATH, body.getPath());
        assertEquals(MESSAGE, body.getError());
        assertEquals(DETAILS, body.getDetails());
    }


    @Test
    void handleServerErrorServiceException() {
        ServerErrorServiceException ex = new InternalServiceException(MESSAGE, DETAILS);
        ResponseEntity<ErrorResponse> response = handler.handleServerErrorServiceException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.getHttpStatus());
        assertEquals(PATH, body.getPath());
        assertEquals(MESSAGE, body.getError());
        assertEquals(DETAILS, body.getDetails());
    }
}