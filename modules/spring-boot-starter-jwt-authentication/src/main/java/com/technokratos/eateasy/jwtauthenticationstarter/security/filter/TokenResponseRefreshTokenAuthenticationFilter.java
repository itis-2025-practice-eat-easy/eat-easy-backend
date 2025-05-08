package com.technokratos.eateasy.jwtauthenticationstarter.security.filter;

import com.technokratos.eateasy.jwtauthenticationstarter.dto.request.RefreshRequest;
import com.technokratos.eateasy.jwtauthenticationstarter.security.authentication.RefreshAuthenticationToken;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiereader.RefreshTokenCookieReader;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.requestmapper.RequestMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import java.util.Objects;

/**
 * Authentication filter for refresh token processing with fingerprint validation.
 * <p>
 * Supports dual token retrieval strategies:
 * <ul>
 *   <li>Cookie-based token extraction (when configured)</li>
 *   <li>Request body token extraction via {@link RefreshRequest}</li>
 * </ul>
 * Validates refresh token against stored fingerprint hash.
 *
 * @see AbstractAuthenticationProcessingFilter
 */
public class TokenResponseRefreshTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final boolean useCookie;
    private final RefreshTokenCookieReader refreshTokenCookieReader;
    private final RequestMapper requestMapper;

    @Builder
    public TokenResponseRefreshTokenAuthenticationFilter(
            String refreshUrl, AuthenticationManager authenticationManager, boolean useCookie,
            RefreshTokenCookieReader refreshTokenCookieReader, RequestMapper requestMapper,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler) {
        super(new AntPathRequestMatcher(refreshUrl, "POST"), authenticationManager);
        this.useCookie = useCookie;
        this.refreshTokenCookieReader = refreshTokenCookieReader;
        this.requestMapper = requestMapper;
        setAuthenticationSuccessHandler(authenticationSuccessHandler);
        setAuthenticationFailureHandler(authenticationFailureHandler);
    }

    /**
     * Attempts authentication by:
     * <ol>
     *   <li>Extracting refresh token from cookie or request body</li>
     *   <li>Retrieving client fingerprint from request body</li>
     *   <li>Creating unauthenticated {@link RefreshAuthenticationToken}</li>
     *   <li>Delegating to authentication manager</li>
     * </ol>
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String refreshToken = obtainRefreshToken(request);
        String fingerprint = obtainFingerprint(request);

        RefreshAuthenticationToken token = RefreshAuthenticationToken.unauthenticated(refreshToken, fingerprint);
        logger.debug("Refreshing token: %s".formatted(refreshToken));

        return this.getAuthenticationManager().authenticate(token);
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
            logger.trace("Refresh token cookie not found in request.");
            return null;
        }
        logger.trace("Refresh token cookie found: %s".formatted(refreshTokenCookie.getValue()));
        return refreshTokenCookie.getValue();
    }


    private String obtainRefreshTokenFromBody(HttpServletRequest request) {
        RefreshRequest refreshRequest = requestMapper.getObjectFromRequest(request, RefreshRequest.class);
        if (Objects.isNull(refreshRequest)) {
            logger.trace("Refresh token not found in request body.");
            return null;
        }

        logger.trace("Refresh token obtained from request body: %s".formatted(refreshRequest.refreshToken()));
        return refreshRequest.refreshToken();
    }

    protected String obtainFingerprint(HttpServletRequest request) {
        RefreshRequest refreshRequest = requestMapper.getObjectFromRequest(request, RefreshRequest.class);
        if (Objects.isNull(refreshRequest)) {
            logger.trace("Fingerprint not found in request body.");
            return "";
        }

        logger.trace("Fingerprint obtained from request body: %s".formatted(refreshRequest.fingerprint()));
        return Objects.requireNonNullElse(refreshRequest.fingerprint(), "");
    }
}
