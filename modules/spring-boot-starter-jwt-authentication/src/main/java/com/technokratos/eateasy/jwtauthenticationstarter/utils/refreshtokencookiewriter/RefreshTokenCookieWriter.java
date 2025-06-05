package com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiewriter;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Interface for managing refresh token cookies in HTTP responses.
 */
public interface RefreshTokenCookieWriter {
    /**
     * Writes refresh token to HTTP-only cookie
     * @param refreshToken JWT refresh token value
     * @param response HTTP response to add cookie
     */
    void write(String refreshToken, HttpServletResponse response);

    /**
     * Removes refresh token cookie from response
     * @param response HTTP response to clear cookie
     */
    void remove(HttpServletResponse response);
}
