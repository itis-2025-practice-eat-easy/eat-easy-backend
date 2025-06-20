package com.technokratos.eateasy.userimpl.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
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
                    .title("User Microservice API")
                    .version("1.0")
                    .description("API для управления пользователями")
                    .contact(new Contact()
                        .name("Technocratos Team")
                        .email("support@technocratos.com"))
                    .license(new License()
                        .name("Apache 2.0")
                        .url("https://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                    .description("User Microservice Wiki")
                    .url("https://example.com/docs"));
    }
}