package com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiereader.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimpleRefreshTokenCookieReaderTest {

    private static final String COOKIE_NAME = "refresh_token";
    private static final String OTHER_COOKIE = "session_id";

    @Mock
    private HttpServletRequest request;
    private SimpleRefreshTokenCookieReader cookieReader;

    @BeforeEach
    void setUp() {
        cookieReader = new SimpleRefreshTokenCookieReader(COOKIE_NAME);
    }

    @Test
    void readNoCookiesShouldReturnNull() {
        when(request.getCookies()).thenReturn(null);
        assertNull(cookieReader.read(request));
    }

    @Test
    void readMatchingCookieShouldReturnsCookie() {
        Cookie expected = new Cookie(COOKIE_NAME, "token");
        when(request.getCookies()).thenReturn(new Cookie[]{expected});

        assertEquals(expected, cookieReader.read(request));
    }

    @Test
    void readMultipleCookiesShouldReturnFirstMatch() {
        Cookie cookie1 = new Cookie(OTHER_COOKIE, "session");
        Cookie cookie2 = new Cookie(COOKIE_NAME, "token");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie1, cookie2});

        assertEquals(cookie2, cookieReader.read(request));
    }

    @Test
    void readNoMatchingCookiesShouldReturnNull() {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie(OTHER_COOKIE, "value")});
        assertNull(cookieReader.read(request));
    }
}