package com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.impl;

import com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token;
import com.technokratos.eateasy.jwtauthenticationstarter.security.userdetails.IdentifiableUserDetails;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.model.RefreshTokenEntity;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.repository.RefreshTokenRepository;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenGeneratorService;
import com.technokratos.eateasy.jwtservice.JwtGeneratorService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token.TokenType.REFRESH;

/**
 * Refresh token generator service implementation with token persistence.
 * <p>
 * Generates refresh tokens with security-enhanced features:
 * <ul>
 *   <li>Persists token metadata with hashed client fingerprint</li>
 *   <li>Includes token ID and user ID (if available) in JWT claims</li>
 *   <li>Configurable token expiration duration</li>
 * </ul>
 *
 * @see RefreshTokenGeneratorService
 */
@Slf4j
@Builder
@RequiredArgsConstructor
public class JwtRefreshTokenGeneratorService implements RefreshTokenGeneratorService {

    private final RefreshTokenRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Token(REFRESH)
    private final JwtGeneratorService jwtGenerator;
    private final Duration expiration;

    private final String userIdClaim;
    private final String refreshTokenIdClaim;

    /**
     * Generates and persists refresh token with security claims
     *
     * @param userDetails authenticated user details
     * @param fingerprint client browser fingerprint for token binding
     * @return signed refresh token containing metadata claims
     */
    @Override
    public String generate(UserDetails userDetails, String fingerprint) {
        UUID tokenId = saveRefreshToken(userDetails.getUsername(), fingerprint);
        Map<String, Object> claims = new HashMap<>();

        putUserIdIfPresent(userDetails, claims);
        putRefreshTokenId(tokenId, claims);

        String token = jwtGenerator.generate(userDetails, claims);

        log.debug("Generated refresh token for user {}: {}", userDetails.getUsername(), token);
        return token;
    }

    private UUID saveRefreshToken(String username, String fingerprint) {
        RefreshTokenEntity entity = createRefreshTokenEntity(username, fingerprint);
        return repository.save(entity);
    }

    private RefreshTokenEntity createRefreshTokenEntity(String username, String fingerprint) {
        return RefreshTokenEntity.builder()
                .fingerprint(fingerprint == null ? null : passwordEncoder.encode(fingerprint))
                .username(username)
                .expiresAt(computeExpirationDate(expiration))
                .build();
    }

    private Instant computeExpirationDate(Duration duration) {
        Instant expirationDate = Instant.now().plus(duration);
        log.trace("Token expiration date: {}", expirationDate);

        return expirationDate;
    }

    private void putUserIdIfPresent(UserDetails userDetails, Map<String, Object> claims) {
        if (!(userDetails instanceof IdentifiableUserDetails<?> identifiable)) {
            log.trace("User details does not implement IdentifiableUserDetails, skipping user id addition");
            return;
        }

        claims.put(userIdClaim, identifiable.getId());
        log.debug("Add user id to refresh token claims: {}", identifiable.getId());
    }

    private void putRefreshTokenId(UUID tokenId, Map<String, Object> claims) {
        claims.put(refreshTokenIdClaim, tokenId);
        log.debug("Add refresh token id to refresh token claims: {}", tokenId);
    }
}
