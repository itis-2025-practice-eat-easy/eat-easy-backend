package com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Extracts and converts token claim values to Spring Security authorities.
 * <p>
 * Processes a claim expected to be a collection of authority strings into
 * {@link GrantedAuthority} instances. Returns empty collection for non-collection
 * claims or invalid authority formats.
 */
@Slf4j
public class AuthoritiesClaimExtractor extends AbstractConfigurableClaimExtractor<Collection<? extends GrantedAuthority>> {

    /**
     * Converts claim value to collection of granted authorities.
     *
     * @param claim JWT claim value (expects Collection of authority strings)
     * @return non-null collection of authorities. Returns empty collection if:
     *         <ul>
     *           <li>Claim is not a collection</li>
     *           <li>Contains null/non-string values</li>
     *         </ul>
     */
    @Override
    protected Collection<? extends GrantedAuthority> mapClaim(Object claim) {
        if (!(claim instanceof Collection<?> authorities)) {
            log.trace("Token does not contain authorities, returning empty collection");
            return Collections.emptyList();
        }

        Collection<? extends GrantedAuthority> grantedAuthorities = authorities.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        log.trace("Extracted authorities from JWT token: {}", grantedAuthorities);
        return grantedAuthorities;
    }
}
