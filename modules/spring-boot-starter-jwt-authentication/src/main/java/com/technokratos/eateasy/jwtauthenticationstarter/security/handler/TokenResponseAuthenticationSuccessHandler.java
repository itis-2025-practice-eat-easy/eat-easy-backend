package com.technokratos.eateasy.jwtauthenticationstarter.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.eateasy.jwtauthenticationstarter.dto.request.FingerprintRequest;
import com.technokratos.eateasy.jwtauthenticationstarter.dto.response.TokenResponse;
import com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.AccessTokenGeneratorService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenGeneratorService;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiewriter.RefreshTokenCookieWriter;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.requestmapper.RequestMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;

/**
 * Authentication success handler that generates and returns tokens.
 * <p>
 * Produces JSON response with access/refresh tokens and optionally sets refresh token
 * in HTTP-only cookie based on configuration.
 * </p>
 */
@Slf4j
@Builder
@RequiredArgsConstructor
public class TokenResponseAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AccessTokenGeneratorService accessTokenGeneratorService;
    private final RefreshTokenGeneratorService refreshTokenGeneratorService;
    private final ObjectMapper objectMapper;
    private final RequestMapper requestMapper;
    private final RefreshTokenCookieWriter cookieWriter;
    private final boolean useCookie;

    /**
     * Handles successful authentication by:
     * <ol>
     *   <li>Generating tokens</li>
     *   <li>Writing tokens to JSON response</li>
     *   <li>Optionally setting refresh token cookie</li>
     * </ol>
     *
     * @throws InternalAuthenticationServiceException for invalid principal type
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.error("Invalid Authentication type. Expected {}, but got {}.", UserDetails.class, authentication.getClass());
            throw new InternalAuthenticationServiceException("Invalid Authentication type. Expected %s, but got %s.".formatted(UserDetails.class, authentication.getClass()));
        }

        TokenResponse tokenResponse = createTokens(userDetails, obtainFingerprint(request));
        log.info("User {} successfully logged in. Generated tokens response: {}", userDetails.getUsername(), tokenResponse);

        writeTokenResponse(response, tokenResponse);
    }

    protected String obtainFingerprint(HttpServletRequest request) {
        FingerprintRequest fingerprintRequest = requestMapper.getObjectFromRequest(request, FingerprintRequest.class);
        if (fingerprintRequest == null) {
            log.trace("Fingerprint request is null, using empty string");
            return "";
        }

        String fingerprint = Objects.requireNonNullElse(fingerprintRequest.fingerprint(), "");
        log.trace("Fingerprint obtained from request: {}", fingerprint);
        return fingerprint;
    }

    protected TokenResponse createTokens(UserDetails userDetails, String fingerprint) {
        String accessToken = accessTokenGeneratorService.generate(userDetails, Collections.emptyMap());
        String refreshToken = refreshTokenGeneratorService.generate(userDetails, fingerprint);

        return new TokenResponse(accessToken, refreshToken);
    }

    private void writeTokenResponse(HttpServletResponse response, TokenResponse tokenResponse) throws ServletException, IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_OK);

        addCookieIfRequired(response, tokenResponse);

        String jsonResponse = objectMapper.writeValueAsString(tokenResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();

        log.debug("Token response sent to client: {}", jsonResponse);

    }

    private void addCookieIfRequired(HttpServletResponse response, TokenResponse tokenResponse) throws ServletException, IOException {
        if (!useCookie) {
            log.trace("Cookie usage is disabled, skipping cookie creation");
            return;
        }

        cookieWriter.write(tokenResponse.refresh(), response);
    }
}
