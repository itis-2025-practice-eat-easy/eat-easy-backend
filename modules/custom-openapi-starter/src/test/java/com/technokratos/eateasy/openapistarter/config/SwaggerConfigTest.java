package com.technokratos.eateasy.openapistarter.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwaggerConfigTest {

    @Test
    void shouldCreateOpenApiWithExpectedValues() {
        SwaggerConfig config = new SwaggerConfig();
        OpenAPI openAPI = config.customOpenAPI();

        Info info = openAPI.getInfo();
        assertEquals("Eat easy", info.getTitle());
        assertEquals("v1 beta", info.getVersion());
        assertTrue(info.getDescription().contains("Eat easy"));

        Contact contact = info.getContact();
        assertEquals("Support Team", contact.getName());
        assertEquals("support@technokratos.com", contact.getEmail());
        assertEquals("https://technokratos.com", contact.getUrl());

        License license = info.getLicense();
        assertEquals("Apache 2.0", license.getName());
        assertEquals("https://www.apache.org/licenses/LICENSE-2.0", license.getUrl());

        assertEquals(1, openAPI.getServers().size());
        Server server = openAPI.getServers().get(0);
        assertEquals("http://localhost:8080", server.getUrl());

        Components components = openAPI.getComponents();
        SecurityScheme securityScheme = components.getSecuritySchemes().get("bearerAuth");
        assertNotNull(securityScheme);
        assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
        assertEquals("bearer", securityScheme.getScheme());
        assertEquals("JWT", securityScheme.getBearerFormat());

        assertTrue(openAPI.getSecurity().stream()
                .anyMatch(sec -> sec.containsKey("bearerAuth")));
    }
}