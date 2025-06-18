package com.technokratos.eateasy.jwtauthenticationstarter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.eateasy.jwtauthenticationstarter.properties.JwtProperties;
import com.technokratos.eateasy.jwtauthenticationstarter.security.filter.TokenResponseRefreshTokenAuthenticationFilter;
import com.technokratos.eateasy.jwtauthenticationstarter.security.handler.ErrorResponseTokenAuthenticationFailureHandler;
import com.technokratos.eateasy.jwtauthenticationstarter.security.handler.TokenResponseAuthenticationSuccessHandler;
import com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.AccessTokenGeneratorService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenGeneratorService;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiereader.RefreshTokenCookieReader;
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
public class RefreshConfig {

    private final JwtProperties jwtProperties;
    private final AuthenticationManager authenticationManager;
    private final AccessTokenGeneratorService accessTokenGeneratorService;
    private final RefreshTokenGeneratorService refreshTokenGeneratorService;
    private final ObjectMapper objectMapper;
    private final RefreshTokenCookieReader cookieReader;
    private final RefreshTokenCookieWriter cookieWriter;
    private final RequestMapper requestMapper;

    @Bean
    public TokenResponseRefreshTokenAuthenticationFilter tokenResponseRefreshTokenAuthenticationFilter(
            @Qualifier("refreshSuccessHandler")
            AuthenticationSuccessHandler refreshSuccessHandler,
            @Qualifier("refreshFailureHandler")
            AuthenticationFailureHandler refreshFailureHandler) {

        return TokenResponseRefreshTokenAuthenticationFilter.builder()
                .refreshUrl(jwtProperties.getRefreshUrl())
                .authenticationManager(authenticationManager)
                .useCookie(jwtProperties.getTokens().getRefresh().isUseCookie())
                .refreshTokenCookieReader(cookieReader)
                .authenticationSuccessHandler(refreshSuccessHandler)
                .authenticationFailureHandler(refreshFailureHandler)
                .requestMapper(requestMapper)
                .build();
    }


    @Bean(name = "refreshSuccessHandler")
    @ConditionalOnMissingBean(name = "refreshSuccessHandler")
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

    @Bean(name = "refreshFailureHandler")
    @ConditionalOnMissingBean(name = "refreshFailureHandler")
    public AuthenticationFailureHandler refreshTokenAuthenticationFailureHandler() {

        return new ErrorResponseTokenAuthenticationFailureHandler(objectMapper, "refresh");
    }
}
