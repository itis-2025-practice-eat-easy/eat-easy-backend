package com.technokratos.eateasy.jwtauthenticationstarter.security.authenticationprovider;

import com.technokratos.eateasy.jwtauthenticationstarter.security.authentication.RefreshAuthenticationToken;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.service.RefreshTokenService;

/**
 * Authentication provider for refresh token validation and rotation.
 * <p>
 * Handles refresh token authentication workflow:
 * <ol>
 *   <li>Validates token signature and fingerprint match</li>
 *   <li>Invalidates used refresh token after successful validation</li>
 *   <li>Loads user details for security context creation</li>
 *   <li>Generates new authenticated token with refreshed credentials</li>
 * </ol>
 *
 * @see AuthenticationProvider
 */
@Slf4j
@Builder
@RequiredArgsConstructor
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {

    private final RefreshTokenService tokenService;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof RefreshAuthenticationToken refreshToken)) {
            return null;
        }

        String jwt = refreshToken.getToken();
        String fingerprint = refreshToken.getFingerprint();
        try {
            tokenService.validate(jwt, fingerprint);
        } catch (CredentialsExpiredException | AuthenticationServiceException e) {
            throw e;
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid JWT token", e);
        }

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(extractUsername(jwt));
            tokenService.invalidate(jwt, fingerprint);

            return RefreshAuthenticationToken.authenticated(jwt, userDetails);
        } catch (UsernameNotFoundException e) {
            log.error("Failed to authenticate refresh token. User not found.", e);
            throw new InternalAuthenticationServiceException("Failed to authenticate refresh token", e);
        }
    }

    private String extractUsername(String jwt) {
        return tokenService.extractUsername(jwt);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RefreshAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
