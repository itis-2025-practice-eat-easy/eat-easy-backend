package com.technokratos.eateasy.authenticationservice.config;

import com.technokratos.eateasy.jwtauthenticationstarter.configurer.SecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AuthenticationConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     Provides an in-memory user details service with a temporary admin user.
     <p>
     This stub implementation will be replaced when the User Microservice becomes available.
     </p>
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(User.withUsername("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("USER")
                .build());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityConfigurer configurer) throws Exception {
        configurer.configure(http);

        http
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/webhooks/**").permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }
}
