package com.technokratos.eateasy.jwtauthenticationstarter.token.access.service.impl;

import com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token;
import com.technokratos.eateasy.jwtauthenticationstarter.security.userdetails.IdentifiableUserDetails;
import com.technokratos.eateasy.jwtservice.JwtGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static com.technokratos.eateasy.jwtauthenticationstarter.qualifier.Token.TokenType.ACCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAccessTokenGeneratorServiceTest {

    private static final String USERNAME = "username";
    private static final String AUTHORITY = "authority";
    private static final String PASSWORD = "password";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String TOKEN = "token";
    private static final String EXTRA_CLAIM_KEY = "key";
    private static final String EXTRA_CLAIM_VALUE = "value";
    private static final Map<String, Object> EXTRA_CLAIMS = Map.of(EXTRA_CLAIM_KEY, EXTRA_CLAIM_VALUE);

    @Mock
    @Token(ACCESS)
    private JwtGeneratorService jwtGenerator;

    @InjectMocks
    private JwtAccessTokenGeneratorService service;

    private IdentifiableUserDetails<UUID> identifiableUserDetails;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        identifiableUserDetails = new TestIdentifiableUserDetails(USER_ID, USERNAME, PASSWORD, AUTHORITY);
        userDetails = User.withUsername(USERNAME)
                .password(PASSWORD)
                .authorities(AUTHORITY)
                .build();
        when(jwtGenerator.generate(any(), any())).thenReturn(TOKEN);
    }

    @Test
    @SuppressWarnings("unchecked")
    void generateWithIdentifiableUserShouldAddUserIdClaimAuthoritiesAndExtraClaims() {
        ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);

        String token = service.generate(identifiableUserDetails, EXTRA_CLAIMS);

        assertEquals(TOKEN, token);

        verify(jwtGenerator).generate(eq(identifiableUserDetails), claimsCaptor.capture());
        Map<String, Object> claims = claimsCaptor.getValue();

        assertAll(
                () -> assertEquals(USER_ID, claims.get(JwtAccessTokenServiceConstants.USER_ID)),
                () -> assertEquals(List.of(AUTHORITY), claims.get(JwtAccessTokenServiceConstants.AUTHORITIES)),
                () -> assertEquals(EXTRA_CLAIM_VALUE, claims.get(EXTRA_CLAIM_KEY))
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void generateWithRegularUserDetailsShouldSkipsUserIdClaim() {
        ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
        when(jwtGenerator.generate(any(), any())).thenReturn("token");

        service.generate(userDetails, Collections.emptyMap());

        verify(jwtGenerator).generate(eq(userDetails), claimsCaptor.capture());
        assertFalse(claimsCaptor.getValue().containsKey(JwtAccessTokenServiceConstants.USER_ID));
    }

    private static class TestIdentifiableUserDetails implements IdentifiableUserDetails<UUID> {
        private final UUID id;
        private final UserDetails delegate;

        private TestIdentifiableUserDetails(UUID id, String username, String password, String authority) {
            this.id = id;
            this.delegate = User.withUsername(username)
                    .password(password)
                    .authorities(authority)
                    .build();
        }

        @Override
        public UUID getId() { return id; }

        @Override public String getUsername() { return delegate.getUsername(); }
        @Override public String getPassword() { return delegate.getPassword(); }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            Collection<GrantedAuthority> authorities = new ArrayList<>(delegate.getAuthorities());

            authorities.add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return null;
                }
            });
            return authorities;
        }
    }

}