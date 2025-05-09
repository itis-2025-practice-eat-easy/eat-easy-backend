package com.technokratos.eateasy.jwtauthenticationstarter.config;

import com.technokratos.eateasy.jwtauthenticationstarter.properties.JwtProperties;
import com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token;
import com.technokratos.eateasy.jwtservice.JwtGeneratorService;
import com.technokratos.eateasy.jwtservice.JwtParserService;
import com.technokratos.eateasy.jwtservice.impl.RsaJwtGeneratorService;
import com.technokratos.eateasy.jwtservice.impl.RsaJwtParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.PrivateKey;
import java.security.PublicKey;

import static com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token.TokenType.ACCESS;
import static com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token.TokenType.REFRESH;

@Configuration
@RequiredArgsConstructor
@ConditionalOnExpression("'${jwt.mode:off}'.equals('off') == false")
@ConditionalOnProperty(prefix = "jwt.tokens", name = {"access.key.algorithm"}, havingValue = "RSA")
public class RsaJwtServiceConfig {

    private final JwtProperties jwtProperties;
    @Token(ACCESS)
    private final PublicKey accessTokenPublicKey;


    @Bean
    @Token(ACCESS)
    public JwtParserService jwtParserService() {
        return new RsaJwtParserService(jwtProperties.getIssuer(), accessTokenPublicKey);
    }


    @Configuration
    @RequiredArgsConstructor
    @ConditionalOnExpression("'${jwt.mode:off}'.equals('server') == true")
    @ConditionalOnProperty(prefix = "jwt.tokens", name = {"access.key.algorithm"}, havingValue = "RSA")
    public class ServerOnlyRsaJwtServiceConfig {


        @Token(REFRESH)
        private final PrivateKey refreshTokenPrivateKey;

        @Token(ACCESS)
        public final PrivateKey accessTokenPrivateKey;

        @Token(REFRESH)
        private final PublicKey refreshTokenPublicKey;


        @Bean
        @Token(REFRESH)
        public JwtGeneratorService refreshJwtGeneratorService() {

            return RsaJwtGeneratorService.builder()
                    .privateKey(refreshTokenPrivateKey)
                    .expiration(jwtProperties.getTokens().getRefresh().getExpiration())
                    .issuer(jwtProperties.getIssuer())
                    .build();
        }


        @Bean
        @Token(ACCESS)
        public JwtGeneratorService accessJwtGeneratorService() {
            return RsaJwtGeneratorService.builder()
                    .privateKey(accessTokenPrivateKey)
                    .expiration(jwtProperties.getTokens().getAccess().getExpiration())
                    .issuer(jwtProperties.getIssuer())
                    .build();
        }

        @Bean
        @Token(REFRESH)
        public JwtParserService refreshJwtParserService() {
            return new RsaJwtParserService(jwtProperties.getIssuer(), refreshTokenPublicKey);
        }
    }

}
