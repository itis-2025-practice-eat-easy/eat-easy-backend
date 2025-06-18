package com.technokratos.eateasy.authenticationservice.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    @Value("${custom.internal.header}")
    private final String internalHeaderName;

    @Value("${custom.internal.secret-key}")
    private final String internalSecretKey;

    @Bean
    public RequestInterceptor internalKeyInterceptor() {
        return template -> template.header(internalHeaderName, internalSecretKey);
    }
}
