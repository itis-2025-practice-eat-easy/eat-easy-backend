package com.technokratos.eateasy.jwtauthenticationstarter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.eateasy.jwtauthenticationstarter.properties.JwtProperties;
import com.technokratos.eateasy.jwtauthenticationstarter.security.filter.TokenResponseUsernamePasswordAuthenticationFilter;
import com.technokratos.eateasy.jwtauthenticationstarter.security.handler.ErrorResponseUsernamePasswordAuthenticationFailureHandler;
import com.technokratos.eateasy.jwtauthenticationstarter.security.handler.TokenResponseAuthenticationSuccessHandler;
import com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.AccessTokenGeneratorService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenService;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiewriter.RefreshTokenCookieWriter;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.requestmapper.RequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "jwt", name = "mode", havingValue = "server")
public class LoginConfig {

    private final AuthenticationManager authenticationManager;
    private final AccessTokenGeneratorService accessTokenGeneratorService;
    private final RefreshTokenService refreshTokenGeneratorService;
    private final ObjectMapper objectMapper;
    private final JwtProperties jwtProperties;
    private final RequestMapper requestMapper;
    private final RefreshTokenCookieWriter cookieWriter;

    @Bean
    public TokenResponseUsernamePasswordAuthenticationFilter loginFilter(
            @Qualifier("loginSuccessHandler") AuthenticationSuccessHandler loginSuccessHandler,
            @Qualifier("loginFailureHandler") AuthenticationFailureHandler loginFailureHandler) {

        return TokenResponseUsernamePasswordAuthenticationFilter.builder()
                .authenticationManager(authenticationManager)
                .authenticationSuccessHandler(loginSuccessHandler)
                .authenticationFailureHandler(loginFailureHandler)
                .loginUrl(jwtProperties.getLoginUrl())
                .requestMapper(requestMapper)
                .build();
    }

    @Bean(name = "loginSuccessHandler")
    @ConditionalOnMissingBean(name = "loginSuccessHandler")
    public AuthenticationSuccessHandler loginSuccessHandler() {

        return TokenResponseAuthenticationSuccessHandler.builder()
                .accessTokenGeneratorService(accessTokenGeneratorService)
                .refreshTokenGeneratorService(refreshTokenGeneratorService)
                .objectMapper(objectMapper)
                .requestMapper(requestMapper)
                .cookieWriter(cookieWriter)
                .useCookie(jwtProperties.getTokens().getRefresh().isUseCookie())
                .build();
    }

    @Bean(name = "loginFailureHandler")
    @ConditionalOnMissingBean(name = "loginFailureHandler")
    public AuthenticationFailureHandler logoutFailureHandler() {
        return new ErrorResponseUsernamePasswordAuthenticationFailureHandler(objectMapper);
    }
}
