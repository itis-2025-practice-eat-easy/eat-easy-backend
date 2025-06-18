package com.technokratos.eateasy.authenticationservice.config;

import com.technokratos.eateasy.common.dto.response.ErrorResponse;
import com.technokratos.eateasy.jwtauthenticationstarter.dto.request.LoginRequest;
import com.technokratos.eateasy.jwtauthenticationstarter.dto.request.RefreshRequest;
import com.technokratos.eateasy.jwtauthenticationstarter.dto.response.TokenResponse;
import com.technokratos.eateasy.jwtauthenticationstarter.properties.JwtProperties;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Collections;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    private static final SecurityScheme BEARER_JWT_SCHEME = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");

    private static final String REFRESH_TOKEN_COOKIE_EXAMPLE =
            "refresh_token=eyJhbGciOiJSUzI1NiJ9...; Max-Age=600; Expires=Tue, 27 May 2025 06:01:50 GMT; Path=/api/v1/auth/refresh; HttpOnly; SameSite=Lax";

    private static final Content TOKEN_RESPONSE = new Content().addMediaType(APPLICATION_JSON_VALUE,
            new MediaType().schema(new Schema<TokenResponse>().$ref("#/components/schemas/TokenResponse")));

    private static final Content LOGIN_REQUEST = new Content().addMediaType(APPLICATION_JSON_VALUE,
            new MediaType().schema(new Schema<LoginRequest>().$ref("#/components/schemas/LoginRequest")));

    private static final Content REFRESH_REQUEST = new Content().addMediaType(APPLICATION_JSON_VALUE,
            new MediaType().schema(new Schema<RefreshRequest>().$ref("#/components/schemas/RefreshRequest")));

    private static final Content ERROR_RESPONSE = new Content().addMediaType(APPLICATION_JSON_VALUE,
            new MediaType().schema(new Schema<ErrorResponse>().$ref("#/components/schemas/ErrorResponse")));

    private static final Parameter REFRESH_TOKEN_COOKIE_PARAM = new Parameter()
            .in("cookie")
            .name("refresh_token")
            .description("Refresh token as cookie")
            .required(false)
            .schema(new Schema<String>().example("eyJhbGciOiJSUzI1NiJ9..."));

    private static final String SET_COOKIE = "Set-Cookie";

    private static final Header REFRESH_TOKEN_COOKIE_HEADER = new Header()
            .description("Refresh token ad HttpOnly cookie")
            .schema(new Schema<String>().example(REFRESH_TOKEN_COOKIE_EXAMPLE));

    private final JwtProperties jwtProperties;

    @Value("${custom.swagger.server}")
    private final String swaggerServer;


    @Bean
    @Order(1)
    public OpenApiCustomizer openApiInfoCustomizer() {
        return openApi -> {
            openApi.getInfo()
                .title("Authentication service API documentation")
                .description("API for managing user authentication and token operations.");

            openApi.setServers(List.of(new Server().url(swaggerServer)));
        };
    }


    @Bean
    @Order(2)
    public OpenApiCustomizer authenticationOpenApiCustomizer() {
        return openAPI -> {

            openAPI.getComponents()
                    .addSecuritySchemes("bearerAuth", BEARER_JWT_SCHEME)
                    .addSchemas("LoginRequest", ModelConverters.getInstance().read(LoginRequest.class).get("LoginRequest"))
                    .addSchemas("TokenResponse", ModelConverters.getInstance().read(TokenResponse.class).get("TokenResponse"))
                    .addSchemas("RefreshRequest", ModelConverters.getInstance().read(RefreshRequest.class).get("RefreshRequest"))
                    .addSchemas("ErrorResponse", ModelConverters.getInstance().read(ErrorResponse.class).get("ErrorResponse"));

            openAPI
                    .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

            Paths paths = openAPI.getPaths();
            paths
                    .addPathItem(jwtProperties.getLoginUrl(), loginRequestPathItem())
                    .addPathItem(jwtProperties.getRefreshUrl(), refreshRequestPathItem())
                    .addPathItem(jwtProperties.getLogoutUrl(), logoutRequestPathItem());

        };
    }



    private static PathItem loginRequestPathItem() {
        return new PathItem()
                .post(new Operation()
                        .operationId("login")
                        .description("Login operation")
                        .security(Collections.emptyList())
                        .requestBody(new RequestBody()
                                .required(true)
                                .content(LOGIN_REQUEST)
                        )
                        .responses(new ApiResponses()
                                .addApiResponse("200",
                                        new ApiResponse()
                                                .description("Login successful")
                                                .content(TOKEN_RESPONSE)
                                                .addHeaderObject(SET_COOKIE, REFRESH_TOKEN_COOKIE_HEADER)
                                )
                                .addApiResponse("401", createErrorResponse("Unauthorized. Login or password is incorrect"))
                                .addApiResponse("403", createErrorResponse("Forbidden. Account is locked, disabled, expired of etc."))
                                .addApiResponse("400", createErrorResponse("Bad Request. Authentication failed"))
                        )
                        .tags(List.of("Authentication API"))
                );
    }

    private static PathItem refreshRequestPathItem() {
        return new PathItem()
                .post(new Operation()
                        .operationId("refresh")
                        .description("Access token refresh operation")
                        .security(Collections.emptyList())
                        .addParametersItem(REFRESH_TOKEN_COOKIE_PARAM)
                        .requestBody(new RequestBody()
                                .required(true)
                                .content(REFRESH_REQUEST)
                        )
                        .responses(new ApiResponses()
                                .addApiResponse("200",
                                        new ApiResponse()
                                                .description("Refresh successful")
                                                .content(TOKEN_RESPONSE)
                                                .addHeaderObject(SET_COOKIE, REFRESH_TOKEN_COOKIE_HEADER)
                                )
                                .addApiResponse("401", createErrorResponse("Unauthorized. Token is invalid or expired"))
                                .addApiResponse("400", createErrorResponse("Bad Request. Authentication failed"))
                        )
                        .tags(List.of("Authentication API"))
                );
    }

    private static PathItem logoutRequestPathItem() {
        return new PathItem()
                .post(new Operation()
                        .operationId("logout")
                        .description("Logout operation")
                        .security(Collections.emptyList())
                        .addParametersItem(REFRESH_TOKEN_COOKIE_PARAM)
                        .requestBody(new RequestBody()
                                .required(true)
                                .content(TOKEN_RESPONSE)
                        )
                        .responses(new ApiResponses()
                                .addApiResponse("200",
                                        new ApiResponse()
                                                .description("Logout successful")
                                )
                        )
                        .tags(List.of("Authentication API"))
                );
    }

    private static ApiResponse createErrorResponse(String description) {
        return new ApiResponse()
                .description(description)
                .content(ERROR_RESPONSE);
    }
}
