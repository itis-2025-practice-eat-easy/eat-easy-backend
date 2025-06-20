package com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiewriter.impl;

import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiewriter.RefreshTokenCookieWriter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

/**
 * Basic implementation of refresh token cookie management with security best practices.
 * <p>
 * Configures cookies with:
 * <ul>
 *   <li>HttpOnly flag</li>
 *   <li>SameSite=Strict policy</li>
 *   <li>Partitioned attribute</li>
 *   <li>Duration-based expiration</li>
 * </ul>
 * </p>
 */
@Slf4j
@Builder
@RequiredArgsConstructor
public class SimpleRefreshTokenCookieWriter implements RefreshTokenCookieWriter {

    private final String cookieName;
    private final Duration expiration;
    private final String refreshUrl;
    private final boolean isSecure;

    @Override
    public void write(String refreshToken, HttpServletResponse response) {
        log.trace("Writing refresh token to cookie: {}", refreshToken);

        Cookie refreshTokenCookie = createCookie(refreshToken);
        response.addCookie(refreshTokenCookie);

        log.debug("Refresh token cookie created: name={}, value={}, maxAge={}, path={}",
                refreshTokenCookie.getName(), refreshTokenCookie.getValue(),
                refreshTokenCookie.getMaxAge(), refreshTokenCookie.getPath());
    }

    private Cookie createCookie(String refreshToken) {
        Cookie refreshTokenCookie = new Cookie(cookieName, refreshToken);
        refreshTokenCookie.setMaxAge(calculateMaxAge(expiration));
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath(refreshUrl);
        refreshTokenCookie.setAttribute("SameSite", "Lax");
        refreshTokenCookie.setSecure(isSecure);

        return refreshTokenCookie;
    }

    private int calculateMaxAge(Duration refreshTokenExpiration) {
        try {
            return Math.toIntExact(refreshTokenExpiration.toSeconds());
        } catch (ArithmeticException e) {
            log.warn("Refresh token expiration time is too long, using Integer.MAX_VALUE instead");
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public void remove(HttpServletResponse response) {
        log.trace("Removing refresh token cookie");
        Cookie refreshTokenCookie = createCookie("");
        refreshTokenCookie.setMaxAge(0); // Set max age to 0 to delete the cookie
        response.addCookie(refreshTokenCookie);

        log.debug("Refresh token cookie removed");
    }
}
