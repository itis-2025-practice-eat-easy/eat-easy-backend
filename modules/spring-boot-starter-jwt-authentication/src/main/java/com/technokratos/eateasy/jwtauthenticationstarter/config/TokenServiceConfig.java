package com.technokratos.eateasy.jwtauthenticationstarter.config;

import com.technokratos.eateasy.jwtauthenticationstarter.properties.JwtProperties;
import com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token;
import com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.AccessTokenGeneratorService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.AccessTokenParserService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.impl.JwtAccessTokenGeneratorService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.impl.JwtAccessTokenParserService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.ConfigurableClaimExtractor;
import com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.impl.UuidClaimExtractor;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.repository.RefreshTokenRepository;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenGeneratorService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenParserService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.impl.CompositeRefreshTokenService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.impl.JwtRefreshTokenGeneratorService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.impl.JwtRefreshTokenParserService;
import com.technokratos.eateasy.jwtservice.JwtGeneratorService;
import com.technokratos.eateasy.jwtservice.JwtParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token.TokenType.ACCESS;
import static com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token.TokenType.REFRESH;


@Configuration
@RequiredArgsConstructor
@ConditionalOnExpression("'${jwt.mode:off}'.equals('off') == false")
public class TokenServiceConfig {

    private final JwtProperties jwtProperties;

    @Bean
    public AccessTokenParserService accessTokenParserService(@Token(ACCESS) JwtParserService accessJwtParserService) {
        return new JwtAccessTokenParserService(accessJwtParserService);
    }


    @Configuration
    @RequiredArgsConstructor
    @ConditionalOnProperty(prefix = "jwt", name = "mode", havingValue = "server")
    public class ServerOnlyTokenServiceConfig {


        private final @Token(ACCESS) JwtGeneratorService accessTokenGeneratorService;
        private final @Token(REFRESH) JwtGeneratorService refreshTokenGeneratorService;
        private final @Token(REFRESH) JwtParserService refreshJwtParserService;
        private final PasswordEncoder passwordEncoder;
        private final RefreshTokenRepository refreshTokenRepository;


        @Bean
        @ConditionalOnMissingBean
        public AccessTokenGeneratorService accessTokenGeneratorService() {
            return JwtAccessTokenGeneratorService.builder()
                    .jwtGenerator(accessTokenGeneratorService)
                    .userIdClaim(jwtProperties.getTokens().getAccess().getClaims().getUserId())
                    .authoritiesClaim(jwtProperties.getTokens().getAccess().getClaims().getAuthorities())
                    .build();
        }

        @ConditionalOnMissingBean
        @Bean(autowireCandidate = false)
        public RefreshTokenGeneratorService refreshTokenGeneratorService() {
            return JwtRefreshTokenGeneratorService.builder()
                    .repository(refreshTokenRepository)
                    .passwordEncoder(passwordEncoder)
                    .jwtGenerator(refreshTokenGeneratorService)
                    .expiration(jwtProperties.getTokens().getRefresh().getExpiration())
                    .refreshTokenIdClaim(jwtProperties.getTokens().getRefresh().getClaims().getRefreshTokenId())
                    .userIdClaim(jwtProperties.getTokens().getRefresh().getClaims().getUserId())
                    .build();
        }

        @ConditionalOnMissingBean
        @Bean(autowireCandidate = false)
        public RefreshTokenParserService refreshTokenParserService() {
            return JwtRefreshTokenParserService.builder()
                    .jwtParser(refreshJwtParserService)
                    .passwordEncoder(passwordEncoder)
                    .repository(refreshTokenRepository)
                    .refreshTokenIdExtractor(refreshTokenIdExtractor())
                    .refreshTokenIdClaim(jwtProperties.getTokens().getRefresh().getClaims().getRefreshTokenId())
                    .build();
        }

        @Bean(autowireCandidate = false)
        public ConfigurableClaimExtractor<UUID> refreshTokenIdExtractor() {
            return new UuidClaimExtractor();
        }

        @Bean
        @ConditionalOnMissingBean
        public RefreshTokenService refreshTokenService() {
            return new CompositeRefreshTokenService(
                    refreshTokenGeneratorService(), refreshTokenParserService(),
                    refreshTokenRepository);
        }
    }
}
