package com.technokratos.eateasy.userimpl.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@TestConfiguration
public class TestRestTemplateConfig {
    @Bean
    public RestTemplateBuilder getRestTemplateBuilder() { return new RestTemplateBuilder()
            .defaultHeader (CONTENT_TYPE, APPLICATION_JSON_VALUE);
    }
}


