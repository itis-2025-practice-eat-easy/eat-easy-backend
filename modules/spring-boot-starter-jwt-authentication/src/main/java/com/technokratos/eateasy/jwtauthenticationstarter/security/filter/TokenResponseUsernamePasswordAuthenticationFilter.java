package com.technokratos.eateasy.jwtauthenticationstarter.security.filter;
import com.technokratos.eateasy.jwtauthenticationstarter.dto.request.LoginRequest;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.requestmapper.RequestMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


/**
 * Custom authentication filter that extracts credentials from JSON request body
 * instead of form parameters. Produces token responses on successful authentication.
 * <p>
 * Extends {@link UsernamePasswordAuthenticationFilter} to override credential extraction
 * behavior using {@link RequestMapper} for JSON deserialization.
 */
public class TokenResponseUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final RequestMapper requestMapper;

    @Builder
    public TokenResponseUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,
                                                             AuthenticationSuccessHandler authenticationSuccessHandler,
                                                             AuthenticationFailureHandler authenticationFailureHandler,
                                                             String loginUrl, RequestMapper requestMapper) {
        super(authenticationManager);
        this.requestMapper = requestMapper;
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(loginUrl, "POST"));
        setAuthenticationSuccessHandler(authenticationSuccessHandler);
        setAuthenticationFailureHandler(authenticationFailureHandler);

    }

    @Override
    protected String obtainPassword(HttpServletRequest request) {
        LoginRequest loginRequest = requestMapper.getObjectFromRequest(request, LoginRequest.class);
        if (loginRequest != null) {
            return loginRequest.password();
        }
        return null;
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        LoginRequest loginRequest = requestMapper.getObjectFromRequest(request, LoginRequest.class);
        if (loginRequest != null) {
            return loginRequest.login();
        }
        return null;
    }
}
