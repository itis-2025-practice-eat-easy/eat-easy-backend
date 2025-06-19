package com.technokratos.eateasy.cart.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private final String swaggerServer;

    public OpenApiConfig(@Value("${custom.swagger.server}") String swaggerServer) {
        this.swaggerServer = swaggerServer;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url(swaggerServer)))
                .info(new Info()
                        .title("Cart Microservice API")
                        .version("1.0")
                        .description("API для управления корзиной пользователя"));
    }
}
