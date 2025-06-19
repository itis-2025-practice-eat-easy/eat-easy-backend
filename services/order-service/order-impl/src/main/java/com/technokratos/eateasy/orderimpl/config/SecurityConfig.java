package com.technokratos.eateasy.orderimpl.config;

import com.technokratos.eateasy.jwtauthenticationstarter.configurer.SecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityConfigurer configurer) throws Exception {
        configurer.configure(http);
        http
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/order-swagger/swagger-ui.html").permitAll()
                        .requestMatchers("/order-swagger/swagger-ui/**").permitAll()
                        .requestMatchers("/order-swagger/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(Customizer.withDefaults());

        return http.build();
    }
}
