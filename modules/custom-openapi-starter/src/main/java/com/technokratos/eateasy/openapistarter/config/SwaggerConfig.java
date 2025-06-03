package com.technokratos.eateasy.openapistarter.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            openApi
                    .info(new Info()
                            .title("Eat easy")
                            .description("API for using the Eat easy service. Provides endpoints for managing products and users.")
                            .version("v1 beta")
                            .termsOfService("https://eateasy.tech/terms")
                            .contact(new Contact()
                                    .name("Support Team")
                                    .email("support@technokratos.com")
                                    .url("https://technokratos.com"))
                            .license(new License()
                                    .name("MIT License")
                                    .url("https://opensource.org/licenses/MIT")))
                    .externalDocs(new ExternalDocumentation()
                            .description("Full API documentation")
                            .url("https://eateasy.tech/docs"))
                    .servers(List.of(
                            new Server()
                                    .url("http://localhost:8080")
                                    .description("Local development server")))
                    .components(new Components()
                            .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                    .type(SecurityScheme.Type.HTTP)
                                    .scheme("bearer")
                                    .bearerFormat("JWT")))
                    .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
        };
    }
}