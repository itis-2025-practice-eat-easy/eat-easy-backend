package com.technokratos.eateasy.jwtauthenticationstarter.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.eateasy.jwtauthenticationstarter.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorResponseUsernamePasswordAuthenticationFailureHandlerTest {

    private static final String JSON_RESPONSE = "jsonResponse";
    private static final String REQUEST_URI = "requestUri";

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter writer;
    @InjectMocks
    private ErrorResponseUsernamePasswordAuthenticationFailureHandler handler;


    @BeforeEach
    void setUp() throws Exception {
         when(objectMapper.writeValueAsString(any())).thenReturn(JSON_RESPONSE);
         when(response.getWriter()).thenReturn(writer);
         when(request.getRequestURI()).thenReturn(REQUEST_URI);
    }

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    void onAuthenticationFailureShouldCorrectlyDetermineHttpStatusAndClientMessage(
            AuthenticationException exception,
            HttpStatus httpStatus, String message) throws Exception {

        ArgumentCaptor<ErrorResponse> errorResponseArgumentCaptor = ArgumentCaptor.forClass(ErrorResponse.class);
        handler.onAuthenticationFailure(request, response, exception);

        verify(objectMapper).writeValueAsString(errorResponseArgumentCaptor.capture());
        ErrorResponse errorResponse = errorResponseArgumentCaptor.getValue();

        assertAll(
                () -> assertEquals(httpStatus.value(), errorResponse.getHttpStatus()),
                () -> assertEquals(message, errorResponse.getError()),
                () -> assertEquals(REQUEST_URI, errorResponse.getPath())
        );

        verify(response).setStatus(eq(httpStatus.value()));
        verify(response).setContentType(eq("application/json"));
        verify(response).setCharacterEncoding(eq("UTF-8"));
        verify(writer).write(JSON_RESPONSE);
    }

    private static Stream<Arguments> exceptionProvider() {
        return Stream.of(
                Arguments.of(new BadCredentialsException(""),
                        HttpStatus.UNAUTHORIZED, "Invalid username or password"),
                Arguments.of(new LockedException(""),
                        HttpStatus.FORBIDDEN, "Account is locked"),
                Arguments.of(new DisabledException(""),
                        HttpStatus.FORBIDDEN, "Account is disabled"),
                Arguments.of(new AuthenticationServiceException(""),
                        HttpStatus.INTERNAL_SERVER_ERROR, "Internal authentication error"),
                Arguments.of(new CredentialsExpiredException(""),
                        HttpStatus.FORBIDDEN, "Credentials expired"),
                Arguments.of(new InsufficientAuthenticationException(""),
                        HttpStatus.BAD_REQUEST, "Authentication failed"),
                Arguments.of(new AccountExpiredException(""),
                        HttpStatus.FORBIDDEN, "Account expired")
        );
    }


}