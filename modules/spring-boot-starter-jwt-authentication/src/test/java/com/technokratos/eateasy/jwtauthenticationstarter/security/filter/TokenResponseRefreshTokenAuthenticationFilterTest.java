package com.technokratos.eateasy.jwtauthenticationstarter.security.filter;

import com.technokratos.eateasy.jwtauthenticationstarter.dto.request.RefreshRequest;
import com.technokratos.eateasy.jwtauthenticationstarter.security.authentication.RefreshAuthenticationToken;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiereader.RefreshTokenCookieReader;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.requestmapper.RequestMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenResponseRefreshTokenAuthenticationFilterTest {

    private static final String REFRESH_URL = "/refresh";
    private static final String REFRESH_TOKEN = "token";
    private static final String FINGERPRINT = "fingerprint";

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RefreshTokenCookieReader refreshTokenCookieReader;
    @Mock
    private RequestMapper requestMapper;
    @Mock
    private AuthenticationSuccessHandler successHandler;
    @Mock
    private AuthenticationFailureHandler failureHandler;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private TokenResponseRefreshTokenAuthenticationFilter noCookieFilter;
    private TokenResponseRefreshTokenAuthenticationFilter useCookieFilter;

    @BeforeEach
    void setUp() {
        noCookieFilter = TokenResponseRefreshTokenAuthenticationFilter.builder()
                .refreshUrl(REFRESH_URL)
                .authenticationManager(authenticationManager)
                .useCookie(false)
                .requestMapper(requestMapper)
                .authenticationSuccessHandler(successHandler)
                .authenticationFailureHandler(failureHandler)
                .build();

        useCookieFilter = TokenResponseRefreshTokenAuthenticationFilter.builder()
                .refreshUrl(REFRESH_URL)
                .authenticationManager(authenticationManager)
                .useCookie(true)
                .refreshTokenCookieReader(refreshTokenCookieReader)
                .requestMapper(requestMapper)
                .authenticationSuccessHandler(successHandler)
                .authenticationFailureHandler(failureHandler)
                .build();
    }

    @Test
    void attemptAuthenticationWithCookieNoRefreshRequestShouldUseEmptyString() {
        when(requestMapper.getObjectFromRequest(request, RefreshRequest.class)).thenReturn(null);

        noCookieFilter.attemptAuthentication(request, response);

        ArgumentCaptor<RefreshAuthenticationToken> tokenCaptor = ArgumentCaptor.forClass(RefreshAuthenticationToken.class);
        verify(requestMapper, times(2)).getObjectFromRequest(request, RefreshRequest.class);
        verify(authenticationManager).authenticate(tokenCaptor.capture());
        verifyNoInteractions(refreshTokenCookieReader);
        RefreshAuthenticationToken token = tokenCaptor.getValue();
        assertNotNull(token);
        assertEquals("", token.getToken());
        assertEquals("", token.getFingerprint());
    }

    @Test
    void attemptAuthenticationWithNullCookieShouldUseRefreshRequest() {
        RefreshRequest refreshRequest = new RefreshRequest(FINGERPRINT, REFRESH_TOKEN);
        when(requestMapper.getObjectFromRequest(request, RefreshRequest.class)).thenReturn(refreshRequest);
        when(refreshTokenCookieReader.read(request)).thenReturn(null);

        useCookieFilter.attemptAuthentication(request, response);

        ArgumentCaptor<RefreshAuthenticationToken> tokenCaptor = ArgumentCaptor.forClass(RefreshAuthenticationToken.class);
        verify(authenticationManager).authenticate(tokenCaptor.capture());
        verify(requestMapper, times(2)).getObjectFromRequest(request, RefreshRequest.class);
        verify(refreshTokenCookieReader, times(1)).read(request);
        RefreshAuthenticationToken token = tokenCaptor.getValue();
        assertNotNull(token);
        assertEquals(REFRESH_TOKEN, token.getToken());
        assertEquals(FINGERPRINT, token.getFingerprint());
    }

    @Test
    void attemptAuthenticationWithCookieShouldUseCookieValue() {
        RefreshRequest refreshRequest = new RefreshRequest(FINGERPRINT, REFRESH_TOKEN);
        when(requestMapper.getObjectFromRequest(request, RefreshRequest.class)).thenReturn(refreshRequest);
        when(refreshTokenCookieReader.read(request)).thenReturn(new Cookie("refresh_token", REFRESH_TOKEN));

        useCookieFilter.attemptAuthentication(request, response);

        ArgumentCaptor<RefreshAuthenticationToken> tokenCaptor = ArgumentCaptor.forClass(RefreshAuthenticationToken.class);
        verify(authenticationManager).authenticate(tokenCaptor.capture());
        verify(requestMapper, times(1)).getObjectFromRequest(request, RefreshRequest.class);
        verify(refreshTokenCookieReader, times(1)).read(request);
        RefreshAuthenticationToken token = tokenCaptor.getValue();
        assertNotNull(token);
        assertEquals(REFRESH_TOKEN, token.getToken());
        assertEquals(FINGERPRINT, token.getFingerprint());
    }
}