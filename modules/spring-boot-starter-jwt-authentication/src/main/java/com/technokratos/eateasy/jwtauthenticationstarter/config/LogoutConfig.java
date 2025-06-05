package com.technokratos.eateasy.jwtauthenticationstarter.config;

import com.technokratos.eateasy.jwtauthenticationstarter.properties.JwtProperties;
import com.technokratos.eateasy.jwtauthenticationstarter.security.handler.RefreshTokenInvalidationLogoutHandler;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenService;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiereader.RefreshTokenCookieReader;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiewriter.RefreshTokenCookieWriter;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.requestmapper.RequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "jwt", name = "mode", havingValue = "server")
public class LogoutConfig {

    private final JwtProperties jwtProperties;
    private final RequestMapper requestMapper;
    private final RefreshTokenCookieWriter cookieWriter;
    private final RefreshTokenCookieReader cookieReader;

    @Bean
    @ConditionalOnMissingBean
    public RefreshTokenInvalidationLogoutHandler logoutHandler(
            @Qualifier("refreshTokenService")
            RefreshTokenService refreshTokenService) {

        return RefreshTokenInvalidationLogoutHandler.builder()
                .useCookie(jwtProperties.getTokens().getRefresh().isUseCookie())
                .requestMapper(requestMapper)
                .refreshTokenCookieReader(cookieReader)
                .refreshTokenCookieWriter(cookieWriter)
                .refreshTokenService(refreshTokenService)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK);
    }

}
