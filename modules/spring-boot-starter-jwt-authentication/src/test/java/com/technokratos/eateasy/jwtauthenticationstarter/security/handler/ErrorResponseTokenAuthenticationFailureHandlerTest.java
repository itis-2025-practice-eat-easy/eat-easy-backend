package com.technokratos.eateasy.jwtauthenticationstarter.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ErrorResponseTokenAuthenticationFailureHandlerTest {

    private static final String TOKEN_NAME = "access";

    @Mock
    private ObjectMapper mapper;
    private ErrorResponseTokenAuthenticationFailureHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ErrorResponseTokenAuthenticationFailureHandler(mapper, TOKEN_NAME);
    }

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    void determineHttpStatus_returnsCorrectStatus(AuthenticationException exception, HttpStatus expectedStatus) {
        assertEquals(expectedStatus, handler.determineHttpStatus(exception));
    }

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    void determineClientMessage_returnsFormattedMessage(AuthenticationException exception,
                                                        HttpStatus ignored, String expectedMessage) {

        assertEquals(expectedMessage, handler.determineClientMessage(exception));
    }

    private static Stream<Arguments> exceptionProvider() {
        return Stream.of(
                Arguments.of(
                        new BadCredentialsException("Invalid"),
                        HttpStatus.UNAUTHORIZED,
                        "Invalid access token"
                ),
                Arguments.of(
                        new CredentialsExpiredException("Expired"),
                        HttpStatus.UNAUTHORIZED,
                        "Access token expired"
                ),
                Arguments.of(
                        new AuthenticationServiceException("Error"),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal authentication error"
                ),
                Arguments.of(
                        new InsufficientAuthenticationException("Other"),
                        HttpStatus.BAD_REQUEST,
                        "Authentication failed"
                )
        );
    }

}