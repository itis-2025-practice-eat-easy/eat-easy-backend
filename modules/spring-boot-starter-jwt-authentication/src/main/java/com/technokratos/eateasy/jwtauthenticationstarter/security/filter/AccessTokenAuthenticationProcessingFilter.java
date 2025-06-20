package com.technokratos.eateasy.jwtauthenticationstarter.security.filter;

import com.technokratos.eateasy.jwtauthenticationstarter.security.authentication.AccessAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Access token authentication filter for token validation.
 * <p>
 * Processes requests with header containing access token,
 * authenticates it through Spring Security's {@link AuthenticationManager},
 * and sets up ${@link SecurityContext} on successful authentication.
 * </p>
 * @see OncePerRequestFilter
 */
@Slf4j
public class AccessTokenAuthenticationProcessingFilter extends OncePerRequestFilter {
    private final String header;

    private final String prefix;
    private final AuthenticationManager authenticationManager;

    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final RequestMatcher accessTokenAuthenticationMatcher;

    @Builder
    public AccessTokenAuthenticationProcessingFilter(String header, String prefix, AuthenticationManager authenticationManager,
                                           AuthenticationFailureHandler authenticationFailureHandler) {
        this.header = header;
        this.prefix = prefix;
        this.authenticationManager = authenticationManager;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.accessTokenAuthenticationMatcher = new HeaderAuthenticationRequestMatcher(header, prefix);
    }

    /**
     * Processes each request to validate access token:
     * <ol>
     *   <li>Checks for valid header</li>
     *   <li>Extracts token from header</li>
     *   <li>Authenticates through ${@link AuthenticationManager}</li>
     *   <li>Sets ${@link SecurityContext} on success</li>
     *   <li>Handles authentication failures</li>
     * </ol>
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (!requireAuthentication(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        AccessAuthenticationToken token = extractToken(request);

        try {
            Authentication authenticated = authenticationManager.authenticate(token);

            if (authenticated == null) {
                log.warn("Authentication not completed: token is null");
                filterChain.doFilter(request, response);
                return;
            }

            onSuccessfulAuthentication(request, response, authenticated, filterChain);
        } catch (InternalAuthenticationServiceException e) {
            log.error("An internal error occurred while trying to authenticate the user.", e);
            onUnsuccessfulAuthentication(request, response, e);
        } catch (AuthenticationException e) {
            onUnsuccessfulAuthentication(request, response, e);
        }
    }

    private boolean requireAuthentication(HttpServletRequest request) {
        if (hasAlreadyAuthenticated()) {
            log.trace("SecurityContextHolder already contains authentication");
            return false;
        }

        return accessTokenAuthenticationMatcher.matches(request);
    }

    private boolean hasAlreadyAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

    private AccessAuthenticationToken extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(header);
        String jwt = authHeader.substring(prefix.length()).trim();

        return AccessAuthenticationToken.unauthenticated(jwt);
    }

    protected void onSuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication,
            FilterChain filterChain) throws ServletException, IOException {

        log.info("JWT authentication successful: {}", authentication);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        log.trace("SecurityContext set with authentication: {}",SecurityContextHolder.getContext().getAuthentication());
        filterChain.doFilter(request, response);
    }

    protected void onUnsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException e) throws IOException, ServletException {

        SecurityContextHolder.clearContext();
        log.debug("Failed to process authentication request: {}", e.getMessage());
        log.trace("SecurityContext cleared");
        log.trace("Handling authentication failure");
        authenticationFailureHandler.onAuthenticationFailure(request, response, e);
    }

    /** Inner class for matching requests with valid Authorization header */
    @Slf4j
    @RequiredArgsConstructor
    private static class HeaderAuthenticationRequestMatcher implements RequestMatcher {
        private final String header;
        private final String prefix;

        @Override
        public boolean matches(HttpServletRequest request) {
            String authHeader = request.getHeader(header);
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(prefix)) {
                log.trace("Authentication header is missing or invalid");
                return false;
            }

            return true;
        }

    }
}
