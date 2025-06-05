package com.technokratos.eateasy.jwtservice.impl;

import com.technokratos.eateasy.jwtservice.JwtGeneratorService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Collections;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA-based JWT generator implementation using JSON Web Token (JWT) specification.
 * <p>
 * This service provides token generation capabilities with configurable expiration,
 * issuer, and support for custom claims. Uses RS256 algorithm for signing tokens.
 * </p>
 * @see JwtGeneratorService
 */
@Slf4j
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RsaJwtGeneratorService implements JwtGeneratorService {


    /**
     * Private key used for signing JWT tokens (RSA algorithm)
     */
    private final PrivateKey privateKey;

    /**
     * Token validity duration from creation time
     */
    private final Duration expiration;

    /**
     * Issuer claim value for generated tokens
     */
    private final String issuer;

    /**
     * Flag indicating whether the generator accepts additional claims
     */
    @Builder.Default
    private boolean supportsExtraClaims = true;

    /**
     * Generates JWT token with user details and additional claims.
     * <p>
     * Token includes standard claims:
     * <ul>
     *   <li>Subject (username from UserDetails)</li>
     *   <li>Issuer</li>
     *   <li>Issued at</li>
     *   <li>Expiration</li>
     * </ul>
     *
     * @param userDetails authenticated user details
     * @param extraClaims additional claims to include in token body
     * @return signed JWT token
     * @throws IllegalArgumentException if extra claims are provided when not supported
     */
    @Override
    public String generate(UserDetails userDetails, @NonNull Map<String, Object> extraClaims) {
        if (!supportsExtraClaims && !Collections.isEmpty(extraClaims)) {
            log.error("Token generation failed: extra claims are not supported");
            throw new IllegalArgumentException("Extra claims are not supported");
        }

        Map<String, Object> claims = new HashMap<>(extraClaims);

        String token = Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .issuer(issuer)
                .expiration(computeExpirationDate(expiration))
                .claims(claims)
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();

        log.debug("Generated token for user {}: {}", userDetails.getUsername(), token);
        return token;
    }

    private Date computeExpirationDate(Duration duration) {
        Date expirationDate = Date.from(Instant.now().plus(duration));
        log.trace("Token expiration date: {}", expirationDate);

        return expirationDate;
    }
}
