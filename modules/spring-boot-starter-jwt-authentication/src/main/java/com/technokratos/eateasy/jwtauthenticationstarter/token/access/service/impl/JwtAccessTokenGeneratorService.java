package com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.impl;

import com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token;
import com.technokratos.eateasy.jwtauthenticationstarter.security.userdetails.IdentifiableUserDetails;
import com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.AccessTokenGeneratorService;
import com.technokratos.eateasy.jwtservice.JwtGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token.TokenType.ACCESS;
import static com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.impl.JwtAccessTokenServiceConstants.AUTHORITIES;
import static com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.impl.JwtAccessTokenServiceConstants.USER_ID;

/**
 * JWT access token generator implementation that enriches tokens with security context claims.
 * <p>
 * Adds standard claims to access tokens:
 * <ul>
 *   <li>User ID (when available via {@link IdentifiableUserDetails})</li>
 *   <li>User authorities</li>
 *   <li>Custom claims from input parameters</li>
 * </ul>
 *
 * @see AccessTokenGeneratorService
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAccessTokenGeneratorService implements AccessTokenGeneratorService {

    @Token(ACCESS)
    private final JwtGeneratorService jwtGenerator;

    /**
     * Generates access token with combined standard and custom claims.
     * <p>
     * Standard claims include:
     * <ul>
     *   <li>{@value JwtAccessTokenServiceConstants#USER_ID} - if user implements {@link IdentifiableUserDetails}</li>
     *   <li>{@value JwtAccessTokenServiceConstants#AUTHORITIES} - normalized list of authorities</li>
     * </ul>
     *
     * @param userDetails authenticated user details
     * @param extraClaims non-null map of additional claims (may override standard claims)
     * @return signed access token
     */
    @Override
    public String generate(UserDetails userDetails, @NonNull Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);

        putUserIdIfPresent(userDetails, claims);
        putAuthorities(userDetails, claims);

        String token = jwtGenerator.generate(userDetails, claims);
        log.debug("Generated access token for user {}: {}", userDetails.getUsername(), token);

        return token;
    }

    private void putUserIdIfPresent(UserDetails userDetails, Map<String, Object> claims) {
        if (!(userDetails instanceof IdentifiableUserDetails<?> identifiable)) {
            log.trace("User details does not implement IdentifiableUserDetails, skipping user id addition");
            return;
        }

        claims.put(USER_ID, identifiable.getId());
        log.debug("Add user id to access token: {}", identifiable.getId());
    }

    private void putAuthorities(UserDetails userDetails, Map<String, Object> claims) {
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .toList();

        claims.put(AUTHORITIES, authorities);
        log.debug("Add authorities to access token: {}", authorities);
    }
}
