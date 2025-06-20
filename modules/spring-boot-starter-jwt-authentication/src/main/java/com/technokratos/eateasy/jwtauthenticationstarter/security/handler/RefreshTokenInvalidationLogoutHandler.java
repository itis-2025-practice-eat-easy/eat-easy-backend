package com.technokratos.eateasy.jwtauthenticationstarter.security.handler;

import com.technokratos.eateasy.jwtauthenticationstarter.dto.request.RefreshRequest;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenService;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiereader.RefreshTokenCookieReader;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiewriter.RefreshTokenCookieWriter;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.requestmapper.RequestMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Logout handler that invalidates refresh tokens during logout.
 * <p>
 * Supports two token retrieval strategies:
 * <ul>
 *   <li>Cookie-based token extraction (when configured)</li>
 *   <li>Request body token extraction via {@link RefreshRequest}</li>
 * </ul>
 * Invalidates tokens and removes cookies according to configuration.
 */
@Slf4j
@Builder
@RequiredArgsConstructor
public class RefreshTokenInvalidationLogoutHandler implements LogoutHandler {
    private final boolean useCookie;
    private final RequestMapper requestMapper;
    private final RefreshTokenCookieReader refreshTokenCookieReader;
    private final RefreshTokenCookieWriter refreshTokenCookieWriter;
    private final RefreshTokenService refreshTokenService;

    /**
     * Performs logout cleanup:
     * <ol>
     *   <li>Extracts refresh token from cookie/body</li>
     *   <li>Invalidates token with stored fingerprint</li>
     *   <li>Removes cookie if configured</li>
     * </ol>
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String refreshToken = obtainRefreshToken(request);

        if (!StringUtils.hasText(refreshToken)) {
            log.debug("Refresh token not found in request");
            return;
        } else {
            invalidateToken(refreshToken, obtainFingerprint(request));
            removeCookieIfRequired(response);
        }

        log.info("User logged out, refresh token invalidated");
    }

    protected String obtainRefreshToken(HttpServletRequest request) {
        String refreshToken = null;
        if (useCookie) {
            refreshToken = obtainRefreshTokenFromCookie(request);
        }

        if (Objects.isNull(refreshToken)) {
            refreshToken = obtainRefreshTokenFromBody(request);
        }

        return Objects.requireNonNullElse(refreshToken, "");
    }

    @Nullable
    private String obtainRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie refreshTokenCookie = refreshTokenCookieReader.read(request);
        if (Objects.isNull(refreshTokenCookie)) {
            log.trace("Refresh token cookie not found in request.");
            return null;
        }
        log.trace("Refresh token cookie found: {}", refreshTokenCookie.getValue());
        return refreshTokenCookie.getValue();
    }

    private String obtainRefreshTokenFromBody(HttpServletRequest request) {
        RefreshRequest refreshRequest = requestMapper.getObjectFromRequest(request, RefreshRequest.class);
        if (Objects.isNull(refreshRequest)) {
            log.trace("Refresh token not found in request body.");
            return null;
        }

        log.trace("Refresh token obtained from request body: {}", refreshRequest.refreshToken());
        return refreshRequest.refreshToken();
    }

    private void invalidateToken(String token, String fingerprint) {
        refreshTokenService.invalidate(token, fingerprint);
        log.debug("Refresh token invalidated");
    }
    protected String obtainFingerprint(HttpServletRequest request) {
        RefreshRequest refreshRequest = requestMapper.getObjectFromRequest(request, RefreshRequest.class);
        if (Objects.isNull(refreshRequest)) {
            log.trace("Fingerprint not found in request body.");
            return "";
        }

        log.trace("Fingerprint obtained from request body: {}", refreshRequest.fingerprint());
        return Objects.requireNonNullElse(refreshRequest.fingerprint(), "");
    }

    protected void removeCookieIfRequired(HttpServletResponse response) {
        if (!useCookie) {
            log.trace("Cookie usage is disabled, skipping cookie removal");
            return;
        }
        refreshTokenCookieWriter.remove(response);
        log.debug("Refresh token cookie removed");
    }
}
