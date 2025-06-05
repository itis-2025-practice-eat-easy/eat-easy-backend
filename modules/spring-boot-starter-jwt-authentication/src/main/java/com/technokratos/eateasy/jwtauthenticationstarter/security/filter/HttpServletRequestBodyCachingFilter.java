package com.technokratos.eateasy.jwtauthenticationstarter.security.filter;

import com.technokratos.eateasy.jwtauthenticationstarter.utils.requestwrapper.CachedBodyHttpServletRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Spring Filter that wraps incoming requests with a {@link CachedBodyHttpServletRequestWrapper}
 * to enable repeated reading of the request body.
 * <p>
 * Applied with a high priority ({@code @Order(-106)}) to ensure caching happens early
 * in the filter chain. This is critical for components that need to read the request
 * body multiple times (e.g., authentication filters).
 * </p>
 */
@Slf4j
public class HttpServletRequestBodyCachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequestWrapper(request);
        log.trace("Request body cached for request: {}", request.getRequestURI());

        filterChain.doFilter(cachedBodyHttpServletRequest, response);
    }
}
