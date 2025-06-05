package com.technokratos.eateasy.jwtauthenticationstarter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.eateasy.jwtauthenticationstarter.properties.JwtProperties;
import com.technokratos.eateasy.jwtauthenticationstarter.security.filter.AccessTokenAuthenticationProcessingFilter;
import com.technokratos.eateasy.jwtauthenticationstarter.security.handler.ErrorResponseTokenAuthenticationFailureHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@RequiredArgsConstructor
@ConditionalOnExpression("'${jwt.mode:off}'.equals('off') == false")
public class AccessConfig {

    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;

    @Bean
    public AccessTokenAuthenticationProcessingFilter accessTokenAuthenticationFilter(
            @Qualifier("accessFailureHandler")
            AuthenticationFailureHandler authenticationFailureHandler) {

        return AccessTokenAuthenticationProcessingFilter.builder()
                .header(jwtProperties.getTokens().getAccess().getHeader())
                .prefix(jwtProperties.getTokens().getAccess().getPrefix())
                .authenticationManager(authenticationManager)
                .authenticationFailureHandler(authenticationFailureHandler)
                .build();
    }

    @Bean(name = "accessFailureHandler")
    @ConditionalOnMissingBean(name = "accessFailureHandler")
    public AuthenticationFailureHandler accessFailureHandler() {

        return new ErrorResponseTokenAuthenticationFailureHandler(objectMapper, "access");
    }
}
