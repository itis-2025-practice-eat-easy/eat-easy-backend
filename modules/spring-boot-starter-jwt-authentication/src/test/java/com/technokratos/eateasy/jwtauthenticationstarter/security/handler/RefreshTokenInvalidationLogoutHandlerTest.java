package com.technokratos.eateasy.jwtauthenticationstarter.security.handler;

import com.technokratos.eateasy.jwtauthenticationstarter.dto.request.RefreshRequest;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenService;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiereader.RefreshTokenCookieReader;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiewriter.RefreshTokenCookieWriter;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.requestmapper.RequestMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenInvalidationLogoutHandlerTest {

    private static final String REFRESH_TOKEN = "refresh.jwt";
    private static final String FINGERPRINT = "client-fp";

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestMapper requestMapper;
    @Mock
    private RefreshTokenCookieReader cookieReader;
    @Mock
    private RefreshTokenCookieWriter cookieWriter;
    @Mock
    private RefreshTokenService tokenService;

    private RefreshTokenInvalidationLogoutHandler useCookieHandler;
    private RefreshTokenInvalidationLogoutHandler noCookieHandler;

    @BeforeEach
    public void setUp() {
        useCookieHandler = createHandler(true);
        noCookieHandler = createHandler(false);
    }

    private RefreshTokenInvalidationLogoutHandler createHandler(boolean useCookie) {
        return RefreshTokenInvalidationLogoutHandler.builder()
                .useCookie(useCookie)
                .requestMapper(requestMapper)
                .refreshTokenCookieReader(cookieReader)
                .refreshTokenCookieWriter(cookieWriter)
                .refreshTokenService(tokenService)
                .build();
    }

    @Test
    void logoutWithCookieTokenShouldInvalidateAndRemoveCookie() throws Exception {
        when(requestMapper.getObjectFromRequest(eq(request), eq(RefreshRequest.class)))
                .thenReturn(null);
        when(cookieReader.read(request)).thenReturn(new Cookie("refresh", REFRESH_TOKEN));

        useCookieHandler.logout(request, response, null);

        verify(tokenService).invalidate(eq(REFRESH_TOKEN), eq(""));
        verify(cookieWriter).remove(response);
    }

    @Test
    void logoutWithBodyTokenShouldInvalidateWithoutCookieRemoval() throws Exception {
        RefreshRequest refreshRequest = new RefreshRequest(FINGERPRINT, REFRESH_TOKEN);
        when(requestMapper.getObjectFromRequest(eq(request), eq(RefreshRequest.class)))
                .thenReturn(refreshRequest);

        noCookieHandler.logout(request, response, null);

        verify(tokenService).invalidate(REFRESH_TOKEN, FINGERPRINT);
        verify(cookieWriter, never()).remove(response);
    }

    @Test
    void logoutMissingTokenShouldSkipInvalidation() throws Exception {
        when(cookieReader.read(request)).thenReturn(null);
        when(requestMapper.getObjectFromRequest(any(), any())).thenReturn(null);

        useCookieHandler.logout(request, response, null);

        verifyNoInteractions(tokenService);
    }
}