package com.technokratos.eateasy.jwtauthenticationstarter.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.eateasy.jwtauthenticationstarter.dto.request.FingerprintRequest;
import com.technokratos.eateasy.jwtauthenticationstarter.dto.response.TokenResponse;
import com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.AccessTokenGeneratorService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenGeneratorService;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiewriter.RefreshTokenCookieWriter;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.requestmapper.RequestMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.PrintWriter;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenResponseAuthenticationSuccessHandlerTest {

    private static final String USERNAME = "testUser";
    private static final String FINGERPRINT = "client-fp";
    private static final String ACCESS_TOKEN = "access.jwt";
    private static final String REFRESH_TOKEN = "refresh.jwt";
    private static final String JSON_RESPONSE = "json-response";

    @Mock(strictness = Mock.Strictness.LENIENT)
    private AccessTokenGeneratorService accessGenerator;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private RefreshTokenGeneratorService refreshGenerator;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private ObjectMapper objectMapper;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private RequestMapper requestMapper;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private RefreshTokenCookieWriter cookieWriter;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private HttpServletRequest request;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private HttpServletRequest noFingerprintRequest;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private HttpServletResponse response;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private PrintWriter writer;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private Authentication authentication;

    private TokenResponseAuthenticationSuccessHandler useCookieHandler;
    private TokenResponseAuthenticationSuccessHandler noCookieHandler;
    private UserDetails userDetails;
    private final ArgumentCaptor<TokenResponse> responseCaptor = ArgumentCaptor.forClass(TokenResponse.class);

    @BeforeEach
    void setUp() throws Exception {
        userDetails = User.withUsername(USERNAME)
                .password("pass")
                .authorities("ROLE_USER")
                .build();

        useCookieHandler = TokenResponseAuthenticationSuccessHandler.builder()
                .accessTokenGeneratorService(accessGenerator)
                .refreshTokenGeneratorService(refreshGenerator)
                .objectMapper(objectMapper)
                .requestMapper(requestMapper)
                .cookieWriter(cookieWriter)
                .useCookie(true)
                .build();

        noCookieHandler = TokenResponseAuthenticationSuccessHandler.builder()
                .accessTokenGeneratorService(accessGenerator)
                .refreshTokenGeneratorService(refreshGenerator)
                .objectMapper(objectMapper)
                .requestMapper(requestMapper)
                .cookieWriter(cookieWriter)
                .useCookie(false)
                .build();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(requestMapper.getObjectFromRequest(noFingerprintRequest, FingerprintRequest.class)).thenReturn(null);
        when(requestMapper.getObjectFromRequest(request, FingerprintRequest.class)).thenReturn(new FingerprintRequest(FINGERPRINT));
        when(accessGenerator.generate(same(userDetails), any())).thenReturn(ACCESS_TOKEN);
        when(refreshGenerator.generate(same(userDetails), any())).thenReturn(REFRESH_TOKEN);
        when(response.getWriter()).thenReturn(writer);
        when(objectMapper.writeValueAsString(any())).thenReturn(JSON_RESPONSE);
    }

    @Test
    void onAuthenticationSuccessWhenPrincipalIsNotUserDetailsShouldThrowInternalAuthenticationServiceException() {
        when(authentication.getPrincipal()).thenReturn(new Object());

        assertThrows(InternalAuthenticationServiceException.class,
                () -> useCookieHandler.onAuthenticationSuccess(request, response, authentication));

        verifyNoInteractions(accessGenerator, refreshGenerator, objectMapper, requestMapper, cookieWriter, response);
    }


    @Test
    void onAuthenticationSuccessWithUserDetailsShouldGenerateTokensAndWritesResponse() throws Exception {

        noCookieHandler.onAuthenticationSuccess(request, response, authentication);

        verify(accessGenerator).generate(same(userDetails), eq(Collections.emptyMap()));
        verify(refreshGenerator).generate(same(userDetails), same(FINGERPRINT));
        verify(response).setContentType(eq("application/json"));
        verify(response).setCharacterEncoding(eq("UTF-8"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(objectMapper).writeValueAsString(responseCaptor.capture());
        verifyNoInteractions(cookieWriter);

        TokenResponse tokenResponse = responseCaptor.getValue();
        assertAll(
                () -> assertEquals(ACCESS_TOKEN, tokenResponse.access()),
                () -> assertEquals(REFRESH_TOKEN, tokenResponse.refresh())
        );
    }

    @Test
    void onAuthenticationSuccessWithCookieShouldWriteCookie() throws Exception {
        useCookieHandler.onAuthenticationSuccess(request, response, authentication);

        verify(cookieWriter).write(REFRESH_TOKEN, response);
    }

    @Test
    void onAuthenticationSuccessWithMissingFingerprintShouldUserEmptyString() throws Exception {
        noCookieHandler.onAuthenticationSuccess(noFingerprintRequest, response, authentication);

        verify(refreshGenerator).generate(userDetails, "");
    }

}