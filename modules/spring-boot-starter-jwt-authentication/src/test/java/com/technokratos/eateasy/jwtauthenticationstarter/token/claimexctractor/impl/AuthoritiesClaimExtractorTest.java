package com.technokratos.eateasy.jwtauthenticationstarter.token.claimexctractor.impl;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthoritiesClaimExtractorTest {
    private final AuthoritiesClaimExtractor extractor = new AuthoritiesClaimExtractor();

    @Test
    void mapClaimWithValidAuthorityStringsShouldReturnGrantedAuthorities() {
        Collection<String> authorities = List.of("ROLE_USER", "ROLE_ADMIN");
        Collection<? extends GrantedAuthority> result = extractor.mapClaim(authorities);

        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertTrue(containsAuthority(result, "ROLE_USER")),
                () -> assertTrue(containsAuthority(result, "ROLE_ADMIN"))
        );
    }

    @Test
    void mapClaimWithNonCollectionClaimShouldReturnEmptyList() {
        Collection<? extends GrantedAuthority> result = extractor.mapClaim("ROLE_USER");
        assertTrue(result.isEmpty());
    }

    @Test
    void mapClaimWithNullElementsShouldFilterNulls() {
        Collection<Object> mixedAuthorities = Arrays.asList("ROLE_USER", null);
        Collection<? extends GrantedAuthority> result = extractor.mapClaim(mixedAuthorities);

        assertEquals(1, result.size());
        assertTrue(containsAuthority(result, "ROLE_USER"));
    }

    private boolean containsAuthority(Collection<? extends GrantedAuthority> authorities, String role) {
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }

}