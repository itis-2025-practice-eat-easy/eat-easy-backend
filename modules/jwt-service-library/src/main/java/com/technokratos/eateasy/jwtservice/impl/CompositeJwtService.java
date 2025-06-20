package com.technokratos.eateasy.jwtservice.impl;

import com.technokratos.eateasy.jwtservice.JwtGeneratorService;
import com.technokratos.eateasy.jwtservice.JwtParserService;
import com.technokratos.eateasy.jwtservice.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

/**
 * Composite JWT service implementation that delegates operations to specialized components.
 * <p>
 * Combines {@link JwtGeneratorService} and {@link JwtParserService} implementations
 * to provide complete JWT lifecycle management while maintaining separation of concerns.
 * Acts as a facade that coordinates token generation and validation workflows.
 */
@RequiredArgsConstructor
public class CompositeJwtService implements JwtService {

    /**
     * JWT generation component delegate
     */
    private final JwtGeneratorService jwtGenerator;

    /**
     * JWT parsing and validation component delegate
     */
    private final JwtParserService jwtParser;


    /**
     * {@inheritDoc}
     * <p>
     * Delegates token generation to configured {@link JwtGeneratorService}
     */
    @Override
    public String generate(UserDetails userDetails, @NonNull Map<String, Object> extraClaims) {
        return jwtGenerator.generate(userDetails, extraClaims);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates token validation to configured {@link JwtParserService}
     */
    @Override
    public void validate(String token) throws AuthenticationException {
        jwtParser.validate(token);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates claims extraction to configured {@link JwtParserService}
     */
    @Override
    public Claims extractAllClaims(String token) throws IllegalArgumentException {
        return jwtParser.extractAllClaims(token);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates username extraction to configured {@link JwtParserService}
     */
    @Override
    public String extractUsername(String token) throws IllegalArgumentException {
        return jwtParser.extractUsername(token);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates username extraction to configured {@link JwtParserService}
     */
    @Override
    public <T> @Nullable T extractClaim(String token, Function<Claims, T> claimExtractor) throws IllegalArgumentException {
        return jwtParser.extractClaim(token, claimExtractor);
    }
}