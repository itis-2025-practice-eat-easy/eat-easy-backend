package com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.impl;

import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.repository.RefreshTokenRepository;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenGeneratorService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenParserService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

/**
 * Composite service implementing complete refresh token lifecycle management.
 * <p>
 * Coordinates token generation, validation, and invalidation by delegating to:
 * <ul>
 *   <li>{@link RefreshTokenGeneratorService} for token creation</li>
 *   <li>{@link RefreshTokenParserService} for validation and claims extraction</li>
 *   <li>{@link RefreshTokenRepository} for token persistence operations</li>
 * </ul>
 * </p>
 *
 * @see RefreshTokenService
 */
@Slf4j
@Builder
@RequiredArgsConstructor
public class CompositeRefreshTokenService implements RefreshTokenService {

    private final RefreshTokenGeneratorService generator;
    private final RefreshTokenParserService parser;
    private final RefreshTokenRepository repository;

    /**
     * {@inheritDoc}
     */
    @Override
    public String generate(UserDetails userDetails, String fingerprint) {
        return generator.generate(userDetails, fingerprint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String extractUsername(String token) throws IllegalArgumentException {
        return parser.extractUsername(token);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(String token, String fingerprint) throws AuthenticationException {
        parser.validate(token, fingerprint);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Invalidates token by removing it from persistence store after validation.
     * Handles both valid and pre-invalidated tokens.
     * </p>
     */
    @Override
    public void invalidate(String token, String fingerprint) {
        try {
            parser.validate(token, fingerprint);
        } catch (AuthenticationException e) {
            log.debug("Token is already invalidated: {}", e.getMessage());
            return;
        }


        UUID refreshTokenId = parser.extractId(token);
        repository.deleteById(refreshTokenId);
        log.debug("Token invalidated: {}", token);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID extractId(String token) throws IllegalArgumentException {
        return parser.extractId(token);
    }
}
