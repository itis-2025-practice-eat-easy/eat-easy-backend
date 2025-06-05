package com.technokratos.eateasy.jwtauthenticationstarter.security.filter;

import com.technokratos.eateasy.jwtauthenticationstarter.security.authentication.AccessAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessTokenAuthenticationProcessingFilterTest {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";
    private static final String VALID_TOKEN = "valid.token";
    private static final String INVALID_TOKEN = "invalid.token";

    @Mock
    private AuthenticationManager authManager;
    @Mock
    private AuthenticationFailureHandler failureHandler;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private Authentication authentication;

    private AccessTokenAuthenticationProcessingFilter filter;

    @BeforeEach
    void setUp() {
        filter = AccessTokenAuthenticationProcessingFilter.builder()
                .header(HEADER)
                .prefix(PREFIX)
                .authenticationManager(authManager)
                .authenticationFailureHandler(failureHandler)
                .build();

    }

    @Test
    void doFilterInternalWhenAlreadyAuthenticatedShouldSkipProcessing() throws Exception {
        SecurityContext authenticatedContext = SecurityContextHolder.createEmptyContext();
        authenticatedContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(authenticatedContext);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(request, response, authManager, failureHandler);
    }

    @Test
    void doFilterInternalWithoutHeaderShouldSkipProcessing() throws Exception {
        when(request.getHeader(any())).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader(HEADER);
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(response, authManager, failureHandler);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternalWithInvalidHeaderPrefixShouldSkipProcessing() throws Exception {
        when(request.getHeader(any())).thenReturn("Invalid prefix" + VALID_TOKEN);

        filter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader(HEADER);
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(response, authManager, failureHandler);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternalWithValidHeaderShouldPutAuthenticationToSecurityContext() throws Exception {
        when(request.getHeader(HEADER)).thenReturn(PREFIX + VALID_TOKEN);
        when(authManager.authenticate(any())).thenReturn(authentication);

        filter.doFilterInternal(request, response, filterChain);
        ArgumentCaptor<AccessAuthenticationToken> tokenCaptor = ArgumentCaptor.forClass(AccessAuthenticationToken.class);

        verify(request, times(2)).getHeader(HEADER);
        verify(authManager).authenticate(tokenCaptor.capture());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(failureHandler);

        AccessAuthenticationToken token = tokenCaptor.getValue();
        assertAll(
                () -> assertEquals(VALID_TOKEN, token.getToken()),
                () -> assertFalse(token.isAuthenticated()),
                () -> assertSame(authentication, SecurityContextHolder.getContext().getAuthentication())
        );
    }

    @Test
    void doFilterInternalWithNullAuthenticationManagerResponseShouldSkipProcessing() throws Exception {
        when(request.getHeader(HEADER)).thenReturn(PREFIX + VALID_TOKEN);
        when(authManager.authenticate(any())).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(request, times(2)).getHeader(HEADER);
        verify(authManager).authenticate(any());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(failureHandler);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @ParameterizedTest
    @MethodSource("authenticationManagerExceptions")
    void doFilterInternalWithAuthenticationManagerThrowsExceptionShouldAbortProcessing(AuthenticationException e) throws Exception {
        when(request.getHeader(HEADER)).thenReturn(PREFIX + VALID_TOKEN);
        when(authManager.authenticate(any())).thenThrow(e);

        filter.doFilterInternal(request, response, filterChain);

        verify(request, times(2)).getHeader(HEADER);
        verify(authManager).authenticate(any());
        verify(failureHandler).onAuthenticationFailure(request, response, e);
        verifyNoInteractions(filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    public static Stream<Arguments> authenticationManagerExceptions() {
        return Stream.of(
                Arguments.of(new InternalAuthenticationServiceException("")),
                Arguments.of(new BadCredentialsException(""))
        );
    }
}