package com.technokratos.eateasy.jwtauthenticationstarter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.eateasy.jwtauthenticationstarter.properties.JwtProperties;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.repository.RefreshTokenRepository;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.repository.impl.InMemoryRefreshTokenRepository;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiereader.RefreshTokenCookieReader;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiereader.impl.SimpleRefreshTokenCookieReader;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiewriter.RefreshTokenCookieWriter;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.refreshtokencookiewriter.impl.SimpleRefreshTokenCookieWriter;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.requestmapper.RequestMapper;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.requestmapper.impl.JacksonObjectMapperRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DefaultBeansConfig {

    @Configuration
    @RequiredArgsConstructor
    @ConditionalOnProperty(prefix = "jwt", name = "mode", havingValue = "server")
    public class ServerOnlyDefalultBeansConfig {

        private final ObjectMapper objectMapper;
        private final JwtProperties jwtProperties;

        @Bean
        @ConditionalOnMissingBean
        public RefreshTokenRepository refreshTokenRepository() {
            return new InMemoryRefreshTokenRepository();
        }

        @Bean
        @ConditionalOnMissingBean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        @ConditionalOnMissingBean
        public RequestMapper requestMapper() {
            return new JacksonObjectMapperRequestMapper(objectMapper);
        }

        @Bean
        @ConditionalOnMissingBean
        public RefreshTokenCookieReader refreshTokenCookieReader() {
            return new SimpleRefreshTokenCookieReader(jwtProperties.getTokens().getRefresh().getCookieName());
        }

        @Bean
        @ConditionalOnMissingBean
        public RefreshTokenCookieWriter refreshTokenCookieWriter() {
            return SimpleRefreshTokenCookieWriter.builder()
                    .cookieName(jwtProperties.getTokens().getRefresh().getCookieName())
                    .expiration(jwtProperties.getTokens().getRefresh().getExpiration())
                    .refreshUrl(jwtProperties.getRefreshUrl())
                    .isSecure(false)
                    .build();
        }
    }
}
