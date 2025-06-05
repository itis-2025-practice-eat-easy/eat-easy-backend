package com.technokratos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                //user-service
                .route("user-service", r -> r
                        .path("/api/v1/users/**")
                        .filters(f -> f.rewritePath("/api/v1/users/(?<segment>.*)",
                                "/api/v1/users/${segment}"))
                        .uri("lb://user-service")
                )
                //product service
                .route("product-service", r -> r
                .path("/api/v1/products/**")
                .filters(f -> f.rewritePath("/api/v1/products/(?<segment>.*)", "/api/v1/products/${segment}"))
                .uri("lb://product-service"))

                //block all other requests
                .route("block-others", r -> r
                        .predicate(exchange -> true) // ловим всё остальное
                        .filters(f -> f.setStatus(HttpStatus.FORBIDDEN))
                        .uri("no://op") // фиктивный URI, не вызывается
                )
                .build();
    }
}

