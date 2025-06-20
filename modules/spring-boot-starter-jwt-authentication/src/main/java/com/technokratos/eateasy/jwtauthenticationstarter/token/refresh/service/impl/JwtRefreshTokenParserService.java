package com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.impl;

import com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token;
import com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.ConfigurableClaimExtractor;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.model.RefreshTokenEntity;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.repository.RefreshTokenRepository;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenParserService;
import com.technokratos.eateasy.jwtservice.JwtParserService;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.UUID;

import static com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token.TokenType.REFRESH;

/**
 * Refresh token validation and parsing service with fingerprint verification.
 * <p>
 * Performs comprehensive refresh token validation including:
 * <ul>
 *   <li>Base JWT signature and expiration validation</li>
 *   <li>Token fingerprint matching with stored hash</li>
 *   <li>Token existence check in persistent storage</li>
 * </ul>
 * </p>
 * @see RefreshTokenParserService
 */
@Slf4j
@Builder
@RequiredArgsConstructor
public class JwtRefreshTokenParserService implements RefreshTokenParserService {

    @Token(REFRESH)
    private final JwtParserService jwtParser;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository repository;
    private final ConfigurableClaimExtractor<? extends UUID> refreshTokenIdExtractor;
    private final String refreshTokenIdClaim;

    @PostConstruct
    public void init() {
        refreshTokenIdExtractor.claimName(refreshTokenIdClaim);
    }

    /** {@inheritDoc} */
    @Override
    public String extractUsername(String token) throws IllegalArgumentException {
        return jwtParser.extractUsername(token);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Validates token integrity and fingerprint match
     * </p>
     * @throws CredentialsExpiredException if token not found in storage
     * @throws BadCredentialsException for fingerprint mismatch
     * @throws AuthenticationException for JWT signature or expiration issues
     */
    @Override
    public void validate(String token, String fingerprint) throws AuthenticationException {
        log.trace("Validating refresh token: {}", token);
        jwtParser.validate(token);

        UUID tokenId = extractIdInternal(token);
        RefreshTokenEntity refreshToken = repository.findById(tokenId)
                .orElseThrow(() -> new CredentialsExpiredException("Refresh token not found"));

        validateFingerprint(refreshToken.getFingerprint(), fingerprint);
        log.trace("Refresh token validated successfully");
    }

    private void validateFingerprint(String expectedFingerprint, String actualFingerprint) {
        if (expectedFingerprint == null && actualFingerprint == null) {
            log.debug("Both expected and actual fingerprints are null");
            return;
        }
        if (expectedFingerprint == null || actualFingerprint == null) {
            log.debug("One of the fingerprints is null: expected {}, got {}", expectedFingerprint, actualFingerprint);
            throw new BadCredentialsException("Invalid refresh token fingerprint");
        }
        if (!passwordEncoder.matches(actualFingerprint, expectedFingerprint)) {
            log.debug("Refresh token fingerprint mismatch");
            throw new BadCredentialsException("Invalid refresh token fingerprint");
        }
    }

    @NonNull
    private UUID extractIdInternal(String token) {
        UUID extractedId = null;
        try {
            extractedId = extractId(token);
        } catch (IllegalArgumentException e) {
            throwFailedToParseRefreshTokenId(token, e);
        }

        if (Objects.isNull(extractedId)) {
            throwFailedToParseRefreshTokenId(token, null);
        }

        log.trace("Extracted refresh token ID: {}", extractedId);
        return extractedId;
    }

    private void throwFailedToParseRefreshTokenId(String token, @Nullable Throwable cause) throws InternalAuthenticationServiceException {
        log.error("Failed to parse refresh token ID from token: {}", token, cause);
        throw new InternalAuthenticationServiceException("Failed to parse refresh token ID", cause);
    }

    /** {@inheritDoc} */
    @Override
    public UUID extractId(String token) throws IllegalArgumentException {
        return refreshTokenIdExtractor.extract(jwtParser.extractAllClaims(token));
    }
}
