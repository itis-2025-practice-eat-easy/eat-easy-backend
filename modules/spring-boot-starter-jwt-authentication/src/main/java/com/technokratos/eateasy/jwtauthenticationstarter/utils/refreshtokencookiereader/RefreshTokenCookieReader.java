package com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiereader;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

/**
 * Interface for reading refresh token cookies from HTTP requests.
 */
public interface RefreshTokenCookieReader {
    /**
     * Retrieves refresh token cookie from HTTP request
     *
     * @param request HTTP request containing cookies
     * @return refresh token cookie or null if not found
     */
    @Nullable
    Cookie read(HttpServletRequest request);
}
