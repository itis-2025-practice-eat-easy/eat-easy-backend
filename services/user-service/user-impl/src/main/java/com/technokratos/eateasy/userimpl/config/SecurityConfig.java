package com.technokratos.eateasy.userimpl.config;

import com.technokratos.eateasy.common.internalkeyvalidator.InternalKeyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
public class SecurityConfig {


    private final String internalRequestHeader;
    private final String internalRequestSecretKey;

    public SecurityConfig(
            @Value("${custom.internal.header}") String internalRequestHeader,
            @Value("${custom.internal.secret-key}") String internalRequestSecretKey) {
        this.internalRequestHeader = internalRequestHeader;
        this.internalRequestSecretKey = internalRequestSecretKey;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InternalKeyValidator internalKeyValidator() {
        return new InternalKeyValidator(internalRequestSecretKey, internalRequestHeader);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(POST, "/api/v1/users").permitAll()
                        .requestMatchers(GET, "/api/v1/users")
                        .access((authentication, context) -> new AuthorizationDecision(internalKeyValidator().hasValidKey(context.getRequest())))
                        .requestMatchers("/user-swagger/swagger-ui.html").permitAll()
                        .requestMatchers("/user-swagger/swagger-ui/**").permitAll()
                        .requestMatchers("/user-swagger/v3/api-docs/**").permitAll()
                        .requestMatchers("/user-swagger/api/v1/webhooks/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}