package com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiewriter.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SimpleRefreshTokenCookieWriterTest {

    private static final String COOKIE_NAME = "refresh_token";
    private static final Duration EXPIRATION = Duration.ofDays(30);
    private static final String REFRESH_PATH = "/api/auth/refresh";
    private static final String TOKEN = "token";

    @Mock
    private HttpServletResponse response;

    private SimpleRefreshTokenCookieWriter cookieWriter;
    private ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

    @BeforeEach
    void setUp() {
        cookieWriter = new SimpleRefreshTokenCookieWriter(COOKIE_NAME, EXPIRATION, REFRESH_PATH, false);
    }

    @Test
    void writeShouldCreateCookie() {

        cookieWriter.write(TOKEN, response);

        verify(response).addCookie(cookieCaptor.capture());
        Cookie cookie = cookieCaptor.getValue();

        assertAll(
                () -> assertEquals(COOKIE_NAME, cookie.getName()),
                () -> assertEquals(TOKEN, cookie.getValue()),
                () -> assertEquals(REFRESH_PATH, cookie.getPath()),
                () -> assertTrue(cookie.isHttpOnly()),
                () -> assertEquals("Lax", cookie.getAttribute("SameSite")),
                () -> assertEquals(EXPIRATION.toSeconds(), cookie.getMaxAge())
        );
    }

    @Test
    void removeShouldSetEmptyCookie() {
        cookieWriter.remove(response);

        verify(response).addCookie(cookieCaptor.capture());
        Cookie cookie = cookieCaptor.getValue();

        assertAll(
                () -> assertEquals(COOKIE_NAME, cookie.getName()),
                () -> assertEquals("", cookie.getValue()),
                () -> assertEquals(0, cookie.getMaxAge())
        );
    }

    @Test
    void writeWhenHaveOverflowShouldUserIntegerMaxValue() {
        Duration hugeDuration = Duration.ofSeconds(Integer.MAX_VALUE + 1L);
        SimpleRefreshTokenCookieWriter writer =
                new SimpleRefreshTokenCookieWriter(COOKIE_NAME, hugeDuration, REFRESH_PATH, false);

        writer.write(TOKEN, response);
        verify(response).addCookie(cookieCaptor.capture());
        Cookie cookie = cookieCaptor.getValue();

        assertEquals(Integer.MAX_VALUE, cookie.getMaxAge());
    }

}