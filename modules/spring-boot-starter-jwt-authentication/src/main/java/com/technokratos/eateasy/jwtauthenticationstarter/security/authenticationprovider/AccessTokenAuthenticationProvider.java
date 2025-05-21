package com.technokratos.eateasy.jwtauthenticationstarter.security.authenticationprovider;

import com.technokratos.eateasy.jwtauthenticationstarter.security.authentication.AccessAuthenticationToken;
import com.technokratos.eateasy.jwtauthenticationstarter.security.userdetails.AccessTokenIdentifiableUserDetails;
import com.technokratos.eateasy.jwtauthenticationstarter.security.userdetails.AccessTokenUserDetails;
import com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.AccessTokenParserService;
import com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.ConfigurableClaimExtractor;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Authentication provider for access token validation and principal resolution.
 * <p>
 * Validates access tokens and constructs authenticated principal with:
 * <ul>
 *   <li>Username from token subject</li>
 *   <li>Authorities from token claims</li>
 *   <li>Optional persistent user ID from claims</li>
 * </ul>
 *
 * @see AuthenticationProvider
 */
@Slf4j
@Builder
@RequiredArgsConstructor
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {

    private final AccessTokenParserService tokenParser;
    private final ConfigurableClaimExtractor<? extends Collection<? extends GrantedAuthority>> authoritiesExtractor;
    private final ConfigurableClaimExtractor<? extends Serializable> userIdExtractor;
    private final String userIdClaim;
    private final String authoritiesClaim;

    @PostConstruct
    protected void init() {
        authoritiesExtractor.claimName(authoritiesClaim);
        userIdExtractor.claimName(userIdClaim);
    }

    /**
     * Validates JWT access token and constructs authenticated principal
     *
     * @throws BadCredentialsException for invalid/malformed tokens
     * @throws CredentialsExpiredException for expired tokens
     * @throws InternalAuthenticationServiceException for claim extraction failures
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof AccessAuthenticationToken accessToken)) {
            return null;
        }

        String jwt = accessToken.getToken();
        try {
            tokenParser.validate(jwt);
        } catch (CredentialsExpiredException | AuthenticationServiceException e) {
            throw e;
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid JWT token", e);
        }

        try {
            UserDetails userDetails = createUserDetails(jwt);

            return AccessAuthenticationToken.authenticated(jwt, userDetails);
        } catch (IllegalArgumentException e) {
            log.error("Failed to authenticate access token", e);
            throw new InternalAuthenticationServiceException("Failed to authenticate access token", e);
        }
    }

    private UserDetails createUserDetails(String jwt) {
        String username = extractUsername(jwt);
        Collection<? extends GrantedAuthority> authorities = extractAuthorities(jwt);
        Serializable userId = extractUserId(jwt);

        if (Objects.isNull(userId)) {
            log.trace("User id is not present in access token, creating AccessTokenUserDetails");
            return AccessTokenUserDetails.of(username, authorities);
        } else {
            log.trace("User id is present in access token, creating AccessTokenIdentifiableUserDetails");
            return AccessTokenIdentifiableUserDetails.of(userId, username, authorities);
        }
    }

    private String extractUsername(String jwt) {
        String username = tokenParser.extractUsername(jwt);
        if (username == null || username.isBlank()) {
            log.error("Access token does not contain a subject");
            throw new BadCredentialsException("JWT token does not contain a subject");
        }

        log.trace("Extracted username from access token: {}", username);
        return username;
    }

    private Collection<? extends GrantedAuthority> extractAuthorities(String jwt) {
        return Objects.requireNonNullElse(tokenParser.extractClaim(jwt, authoritiesExtractor), Collections.emptyList());
    }

    @Nullable
    private Serializable extractUserId(String jwt) {
        return tokenParser.extractClaim(jwt, userIdExtractor);
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(Class<?> authentication) {
        return AccessAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
