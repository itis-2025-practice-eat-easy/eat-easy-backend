package com.technokratos.eateasy.jwtauthenticationstarter;

import com.technokratos.eateasy.jwtauthenticationstarter.configurer.SecurityConfigurer;
import com.technokratos.eateasy.jwtauthenticationstarter.properties.JwtProperties;
import com.technokratos.eateasy.jwtauthenticationstarter.security.filter.AccessTokenAuthenticationProcessingFilter;
import com.technokratos.eateasy.jwtauthenticationstarter.security.filter.TokenResponseRefreshTokenAuthenticationFilter;
import com.technokratos.eateasy.jwtauthenticationstarter.security.filter.TokenResponseUsernamePasswordAuthenticationFilter;
import com.technokratos.eateasy.jwtauthenticationstarter.security.handler.RefreshTokenInvalidationLogoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@ComponentScan(basePackages = "com.technokratos.eateasy.jwtauthenticationstarter.config")
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    @Configuration
    @RequiredArgsConstructor
    @ConditionalOnProperty(prefix = "jwt", name = "mode", havingValue = "server")
    public static class ServerSecurityConfig {

        private final TokenResponseUsernamePasswordAuthenticationFilter tokenResponseUsernamePasswordAuthenticationFilter;
        private final TokenResponseRefreshTokenAuthenticationFilter tokenResponseRefreshTokenAuthenticationFilter;
        private final AccessTokenAuthenticationProcessingFilter accessTokenAuthenticationFilter;
        private final RefreshTokenInvalidationLogoutHandler refreshTokenInvalidationLogoutHandler;
        private final LogoutSuccessHandler logoutSuccessHandler;
        private final JwtProperties jwtProperties;

        @Bean
        public SecurityConfigurer securityConfigurer() {
            return http -> http
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .addFilterAfter(tokenResponseUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .addFilterAfter(accessTokenAuthenticationFilter, TokenResponseUsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(tokenResponseRefreshTokenAuthenticationFilter, AccessTokenAuthenticationProcessingFilter.class)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/error").permitAll()
                    )
                    .httpBasic(AbstractHttpConfigurer::disable)
                    .formLogin(AbstractHttpConfigurer::disable)
                    .logout(logout -> logout
                            .logoutUrl(jwtProperties.getLogoutUrl())
                            .addLogoutHandler(refreshTokenInvalidationLogoutHandler)
                            .logoutSuccessHandler(logoutSuccessHandler)
                    );
        }
    }

    @Configuration
    @RequiredArgsConstructor
    @ConditionalOnProperty(prefix = "jwt", name = "mode", havingValue = "client")
    public static class ClientSecurityConfig {

        private final AccessTokenAuthenticationProcessingFilter accessTokenAuthenticationFilter;


        @Bean
        public SecurityConfigurer securityConfigurer() {
            return http -> http
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .addFilterAfter(accessTokenAuthenticationFilter, TokenResponseUsernamePasswordAuthenticationFilter.class)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/error").permitAll()
                    )
                    .httpBasic(AbstractHttpConfigurer::disable)
                    .formLogin(AbstractHttpConfigurer::disable)
                    .logout(AbstractHttpConfigurer::disable);
        }
    }

    @Configuration
    @RequiredArgsConstructor
    @ConditionalOnProperty(prefix = "jwt", name = "mode", havingValue = "off")
    public static class DisabledSecurityConfig {

        @Bean
        public SecurityConfigurer securityConfigurer() {
            return http -> {};
        }
    }
}
