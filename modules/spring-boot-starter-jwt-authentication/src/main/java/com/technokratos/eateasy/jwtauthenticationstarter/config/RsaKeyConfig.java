package com.technokratos.eateasy.jwtauthenticationstarter.config;

import com.technokratos.eateasy.jwtauthenticationstarter.properties.JwtProperties;
import com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.rsakey.RsaKeysUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;

import static com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token.TokenType.ACCESS;
import static com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token.TokenType.REFRESH;


@Configuration
@RequiredArgsConstructor
@ConditionalOnExpression("'${jwt.mode:off}'.equals('off') == false")
@ConditionalOnProperty(prefix = "jwt.tokens", name = {"access.key.algorithm"}, havingValue = "RSA")
public class RsaKeyConfig {

    private final JwtProperties jwtProperties;


    @Bean
    @Token(ACCESS)
    @ConditionalOnProperty(prefix = "jwt", name = "tokens.access.key.algorithm", havingValue = "RSA")
    public PublicKey accessPublicKey() throws IOException, GeneralSecurityException {
        return RsaKeysUtils.loadPublicKey(jwtProperties.getTokens().getAccess().getKey().getPublicKey());
    }


    @Configuration
    @RequiredArgsConstructor
    @ConditionalOnExpression("'${jwt.mode:off}'.equals('off') == false")
    @ConditionalOnProperty(prefix = "jwt.tokens", name = {"access.key.algorithm", "refresh.key.algorithm"}, havingValue = "RSA")
    public class ServerOnlyKeyConfig {

        @Bean
        @Token(ACCESS)
        public PrivateKey accessPrivateKey() throws IOException, GeneralSecurityException {
            return RsaKeysUtils.loadPrivateKey(jwtProperties.getTokens().getAccess().getKey().getPrivateKey());
        }


        @Bean
        @Token(REFRESH)
        public PrivateKey refreshPrivateKey() throws IOException, GeneralSecurityException {
            return RsaKeysUtils.loadPrivateKey(jwtProperties.getTokens().getRefresh().getKey().getPrivateKey());
        }

        @Bean
        @Token(REFRESH)
        public PublicKey refreshPublicKey() throws IOException, GeneralSecurityException {
            return RsaKeysUtils.loadPublicKey(jwtProperties.getTokens().getRefresh().getKey().getPublicKey());
        }
    }
}
