package com.technokratos.eateasy.jwtservice.impl;

import com.technokratos.eateasy.jwtservice.JwtParserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;

import java.security.PublicKey;

/**
 * RSA-based JWT parser and validator implementation using JSON Web Token (JWT) specification.
 * <p>
 * Provides token validation and claims extraction functionality using RS256 signature verification.
 * Validates token structure, expiration, issuer claim, and required subject presence.
 * </p>
 *
 * @see JwtParserService
 */
@Slf4j
@Builder
@RequiredArgsConstructor
public class RsaJwtParserService implements JwtParserService {

    /**
     * Expected issuer claim value for token validation
     */
    private final String issuer;

    /**
     * Public key used for signature verification
     */
    private final PublicKey publicKey;


    /**
     * Validates JWT token integrity and compliance with security requirements.
     * <p>
     * Performs following validations:
     * <ul>
     *   <li>Token structure and format</li>
     *   <li>Digital signature verification</li>
     *   <li>Expiration time check</li>
     *   <li>Issuer claim match</li>
     *   <li>Subject presence</li>
     * </ul>
     *
     * @param token JWT token to validate
     * @throws AuthenticationException with specific subtype based on validation failure:
     *         <ul>
     *           <li>CredentialsExpiredException - expired token</li>
     *           <li>InsufficientAuthenticationException - missing subject</li>
     *           <li>BadCredentialsException - other validation failures</li>
     *         </ul>
     */
    @Override
    public void validate(String token) throws AuthenticationException {
        log.trace("Validating token: {}", token);

        if (token == null || token.isBlank()) {
            log.debug("Token validation failed: token is null or blank");
            throw new BadCredentialsException("Token is null or blank");
        }

        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(publicKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token);

            Claims claims = claimsJws.getPayload();

            if (claims.getExpiration() == null) {
                log.debug("Token validation failed: Expiration claim is missing");
                throw new BadCredentialsException("Token expiration claim is missing");
            }

            String subject = claims.getSubject();
            if (!StringUtils.hasText(subject)) {
                log.debug("Token subject is missing or blank; claims={}", claims);
                throw new InsufficientAuthenticationException("Token subject is missing or blank");
            }

        } catch (ExpiredJwtException e) {
            log.debug("Token validation failed: Token expired at {}",  e.getClaims().getExpiration());
            throw new CredentialsExpiredException("Token has expired", e);

        } catch (UnsupportedJwtException e) {
            log.debug("Token validation failed: Unsupported JWT format - {}", e.getMessage());
            throw new BadCredentialsException("Unsupported token format", e);

        } catch (MalformedJwtException e) {
            log.debug("Token validation failed: Malformed JWT - {}", e.getMessage());
            throw new BadCredentialsException("Invalid token structure", e);

        } catch (IncorrectClaimException e) {
            log.debug("Token validation failed: Claim validation failed. Got '{}'='{}'",
                    e.getClaimName(), e.getClaimValue());
            throw new BadCredentialsException("Invalid token claims", e);

        } catch (SignatureException e) {
            log.debug("Token validation failed: Signature verification failed - {}", e.getMessage());
            throw new BadCredentialsException("Invalid token signature", e);
        } catch (JwtException e) {
            log.debug("Token validation failed: General JWT exception - {}", e.getMessage());
            throw new BadCredentialsException("Token validation failed", e);
        }

        log.debug("Token validation successful for: {}", token);
    }


    /**
     * Extracts all claims from JWT token body after successful signature verification.
     *
     * @param token valid JWT token
     * @return map containing all token claims
     * @throws IllegalArgumentException if token is invalid or claims extraction fails
     */
    @Override
    public Claims extractAllClaims(String token) throws IllegalArgumentException {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token);

            return claimsJws.getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Failed to extract claims from token: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to extract claims from token", e);
        }
    }
}
