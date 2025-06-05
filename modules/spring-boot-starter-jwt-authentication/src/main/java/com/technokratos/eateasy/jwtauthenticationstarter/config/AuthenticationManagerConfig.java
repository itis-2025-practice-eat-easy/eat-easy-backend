package com.technokratos.eateasy.jwtauthenticationstarter.config;

import com.technokratos.eateasy.jwtauthenticationstarter.security.authenticationprovider.AccessTokenAuthenticationProvider;
import com.technokratos.eateasy.jwtauthenticationstarter.security.authenticationprovider.RefreshTokenAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@RequiredArgsConstructor
@ConditionalOnExpression("'${jwt.mode:off}'.equals('off') == false")
public class AuthenticationManagerConfig {

    private final AccessTokenAuthenticationProvider accessTokenAuthenticationProvider;
    private final HttpSecurity http;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "jwt", name = "mode", havingValue = "server")
    public AuthenticationManager serverAuthenticationManager(RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(accessTokenAuthenticationProvider);
        authenticationManagerBuilder.authenticationProvider(refreshTokenAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "jwt", name = "mode", havingValue = "client")
    public AuthenticationManager clientAuthenticationManager() throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(accessTokenAuthenticationProvider);
        return authenticationManagerBuilder.build();
    }
}
