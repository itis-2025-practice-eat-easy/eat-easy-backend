package com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiereader.impl;

import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiereader.RefreshTokenCookieReader;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Cookie reader implementation that searches for a specific refresh token cookie by name.
 * <p>
 * Scans request cookies and returns the first match with the configured cookie name.
 */
@Slf4j
@RequiredArgsConstructor
public class SimpleRefreshTokenCookieReader implements RefreshTokenCookieReader {

    private final String cookieName;

    @Override
    public Cookie read(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (Objects.isNull(cookies)) {
            log.trace("No cookies found in the request.");
            return null;
        }

        return retrieveCookie(cookies);
    }

    private Cookie retrieveCookie(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (isLookingForCookie(cookie)) {
                log.trace("Found refresh token cookie: name={}, value={}", cookieName, cookie.getValue());
                return cookie;
            }
        }
        log.trace("No cookie found with name: {}", cookieName);
        return null;
    }

    private boolean isLookingForCookie(Cookie cookie) {
        return cookieName.equals(cookie.getName());
    }

}
