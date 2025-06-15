package com.technokratos.eateasy.authenticationservice.config;

import com.technokratos.eateasy.jwtauthenticationstarter.configurer.SecurityConfigurer;
import com.technokratos.eateasy.jwtauthenticationstarter.properties.JwtProperties;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiewriter.RefreshTokenCookieWriter;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiewriter.impl.SimpleRefreshTokenCookieWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final JwtProperties jwtProperties;
    @Value("${custom.use-https}")
    private final boolean useHttps;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RefreshTokenCookieWriter refreshTokenCookieWriter() {
        return SimpleRefreshTokenCookieWriter.builder()
                .cookieName(jwtProperties.getTokens().getRefresh().getCookieName())
                .expiration(jwtProperties.getTokens().getRefresh().getExpiration())
                .refreshUrl(jwtProperties.getRefreshUrl())
                .isSecure(useHttps)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityConfigurer configurer) throws Exception {
        configurer.configure(http);

        http
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/authentication-swagger/swagger-ui/**").permitAll()
                        .requestMatchers("/authentication-swagger/v3/api-docs/**").permitAll()
                        .requestMatchers("/authentication-swagger/api/v1/webhooks/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/v1/auth/test").permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }
}
