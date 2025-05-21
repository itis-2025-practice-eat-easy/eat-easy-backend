package com.technokratos.eateasy.jwtauthenticationstarter.config;

import com.technokratos.eateasy.jwtauthenticationstarter.properties.JwtProperties;
import com.technokratos.eateasy.jwtauthenticationstarter.security.authenticationprovider.AccessTokenAuthenticationProvider;
import com.technokratos.eateasy.jwtauthenticationstarter.security.authenticationprovider.RefreshTokenAuthenticationProvider;
import com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.AccessTokenParserService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.ConfigurableClaimExtractor;
import com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.impl.AuthoritiesClaimExtractor;
import com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.impl.UuidClaimExtractor;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;
import java.util.Collection;

@Configuration
@RequiredArgsConstructor
@ConditionalOnExpression("'${jwt.mode:off}'.equals('off') == false")
public class AuthenticationProvidersConfig {

    private final JwtProperties jwtProperties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "jwt", name = "mode", havingValue = "server")
    public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "jwt", name = "mode", havingValue = "server")
    public RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider(RefreshTokenService refreshTokenService, UserDetailsService userDetailsService) {
        return new RefreshTokenAuthenticationProvider(refreshTokenService, userDetailsService);
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessTokenAuthenticationProvider accessTokenAuthenticationProvider(
            AccessTokenParserService parserService,
            @Qualifier("authoritiesExtractor")
            ConfigurableClaimExtractor<Collection<? extends GrantedAuthority>> authoritiesExtractor,
            @Qualifier("userIdExtractor")
            ConfigurableClaimExtractor<? extends Serializable> userIdExtractor) {
        return AccessTokenAuthenticationProvider.builder()
                .tokenParser(parserService)
                .userIdExtractor(userIdExtractor)
                .authoritiesExtractor(authoritiesExtractor)
                .authoritiesClaim(jwtProperties.getTokens().getAccess().getClaims().getAuthorities())
                .userIdClaim(jwtProperties.getTokens().getAccess().getClaims().getUserId())
                .build();
    }

    @Bean(name = "userIdExtractor")
    @ConditionalOnMissingBean
    public UuidClaimExtractor userIdClaimExtractor() {
        return new UuidClaimExtractor();
    }

    @Bean(name = "authoritiesExtractor")
    @ConditionalOnMissingBean
    public AuthoritiesClaimExtractor authoritiesExtractor() {
        return new AuthoritiesClaimExtractor();
    }
}
