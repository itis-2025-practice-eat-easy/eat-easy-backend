package com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.impl;

import com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token;
import com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.AccessTokenParserService;
import com.technokratos.eateasy.jwtservice.JwtParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

import static com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token.TokenType.ACCESS;

/**
 * Access token-specific implementation of JWT parsing and validation operations.
 * <p>
 * Acts as a facade delegating to {@link JwtParserService} while providing
 * access token context for logging and monitoring purposes.
 * </p>
 * @see AccessTokenParserService
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAccessTokenParserService implements AccessTokenParserService {

    /**
     * Access token-qualified JWT parser delegate
     */
    @Token(ACCESS)
    private final JwtParserService jwtParser;

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to access token-specific parser implementation
     * </p>
     */
    @Override
    public String extractUsername(String token) throws IllegalArgumentException {
        return jwtParser.extractUsername(token);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to access token-specific parser implementation
     * </p>
     */
    @Override
    public void validate(String token) throws AuthenticationException {
        log.trace("Validating access token: {}", token);

        jwtParser.validate(token);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to access token-specific parser implementation
     * </p>
     */
    @Override
    public Map<String, Object> extractAllClaims(String token) throws IllegalArgumentException {
        return jwtParser.extractAllClaims(token);
    }
}
