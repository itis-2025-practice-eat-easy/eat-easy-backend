package com.technokratos.eateasy.jwtauthenticationstarter.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Configuration properties for authentication setup.
 * <p>
 * Contains settings for authentication endpoints, token parameters,
 * and cryptographic key configuration. Validates property values
 * and provides sensible defaults.
 *
 * @see ConfigurationProperties
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Authentication starter operation mode
     */
    @NotNull
    private MODE mode = MODE.OFF;

    /**
     * Authentication endpoint paths
     */
    @NotEmpty
    private String loginUrl = "/auth/login";

    @NotEmpty
    private String refreshUrl = "auth/refresh";

    @NotEmpty
    private String logoutUrl = "/auth/logout";

    /**
     * Issuer claim value for generated tokens
     */
    @NotEmpty
    private String issuer = "app";

    /**
     * Token configuration parameters
     */
    @NotNull
    private TokenProperties tokens = new TokenProperties();

    /**
     * Authentication starter operation modes:
     * <ul>
     *   <li>OFF - Authentication disabled</li>
     *   <li>SERVER - Full validation and generation</li>
     *   <li>CLIENT - Validation only</li>
     * </ul>
     */
    public enum MODE {
        OFF, SERVER, CLIENT
    }

    /**
     * Container for token-specific configurations
     */
    @Getter
    @Setter
    public static class TokenProperties {
        /** Access token parameters */
        @NotNull
        private AccessTokenConfig access = new AccessTokenConfig();

        /** Refresh token parameters */
        private RefreshTokenConfig refresh = new RefreshTokenConfig();
    }

    /**
     * Base token configuration parameters
     */
    @Getter
    @Setter
    public static class TokenConfig {
        /**
         * Token validity duration
         */
        @DurationUnit(ChronoUnit.MINUTES)
        private Duration expiration;

        /** Cryptographic key configuration */
        private KeyProperties key = new KeyProperties();
    }

    /**
     * Access token specific configuration
     */
    @Getter
    @Setter
    public static class AccessTokenConfig extends TokenConfig {
        /** HTTP header name for access token */
        @NotEmpty
        private String header = "Authorization";

        /** Authorization scheme prefix */
        @NotEmpty
        private String prefix = "Bearer ";
    }

    /**
     * Refresh token specific configuration
     */
    @Getter
    @Setter
    public static class RefreshTokenConfig extends TokenConfig {
        /** Enable cookie-based refresh token storage */
        private boolean useCookie = true;

        /** Cookie name for refresh token storage */
        private String cookieName = "refresh_token";
    }

    /**
     * Cryptographic key configuration
     */
    @Getter
    @Setter
    public static class KeyProperties {
        /** JWT signature algorithm (e.g. RS256, HS512) */
        @NotEmpty
        private String algorithm;

        /** Public key resource path (asymmetric algorithms) */
        private Resource publicKey;

        /** Private key resource path (asymmetric algorithms) */
        private Resource privateKey;

        /** Secret key resource path (symmetric algorithms) */
        private Resource secretKey;
    }
}