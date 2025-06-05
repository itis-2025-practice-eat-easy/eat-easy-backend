package com.technokratos.eateasy.jwtauthenticationstarter.security.filter;

import com.technokratos.eateasy.jwtauthenticationstarter.dto.request.LoginRequest;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.requestmapper.RequestMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenResponseUsernamePasswordAuthenticationFilterTest {

    private final static String LOGIN_URL = "loginUrl";
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";

    @Mock(strictness = Mock.Strictness.LENIENT)
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private AuthenticationManager authenticationManager;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private RequestMapper requestMapper;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private HttpServletRequest nonNullRequest;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private HttpServletRequest nullRequest;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private LoginRequest loginRequest;


    private TokenResponseUsernamePasswordAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = TokenResponseUsernamePasswordAuthenticationFilter.builder()
                .authenticationManager(authenticationManager)
                .authenticationSuccessHandler(authenticationSuccessHandler)
                .authenticationFailureHandler(authenticationFailureHandler)
                .loginUrl(LOGIN_URL)
                .requestMapper(requestMapper)
                .build();

        when(requestMapper.getObjectFromRequest(nonNullRequest, LoginRequest.class)).thenReturn(loginRequest);
        when(requestMapper.getObjectFromRequest(nullRequest, LoginRequest.class)).thenReturn(null);
        when(loginRequest.login()).thenReturn(USERNAME);
        when(loginRequest.password()).thenReturn(PASSWORD);
    }


    @Test
    void obtainUsernameWithNotNullLoginRequestShouldReturnUsername() {
        String username = filter.obtainUsername(nonNullRequest);
        assertEquals(USERNAME, username);

        verify(requestMapper).getObjectFromRequest(nonNullRequest, LoginRequest.class);
        verify(loginRequest).login();
    }

    @Test
    void obtainUsernameWithNullLoginRequestShouldReturnNull() {
        String username = filter.obtainUsername(nullRequest);

        assertNull(username);
        verify(requestMapper).getObjectFromRequest(nullRequest, LoginRequest.class);
    }

    @Test
    void obtainPasswordWithNotNullLoginRequestShouldReturnPassword() {
        String password = filter.obtainPassword(nonNullRequest);
        assertEquals(PASSWORD, password);

        verify(requestMapper).getObjectFromRequest(nonNullRequest, LoginRequest.class);
        verify(loginRequest).password();
    }

    @Test
    void obtainPasswordWithNullLoginRequestShouldReturnNull() {
        String password = filter.obtainPassword(nullRequest);

        assertNull(password);
        verify(requestMapper).getObjectFromRequest(nullRequest, LoginRequest.class);
    }

    @Test
    void constructorShouldSetRequestMather() {
        Field field = ReflectionUtils.findField(TokenResponseUsernamePasswordAuthenticationFilter.class, "requiresAuthenticationRequestMatcher");
        ReflectionUtils.makeAccessible(field);
        AntPathRequestMatcher requestMatcher = (AntPathRequestMatcher) ReflectionUtils.getField(field, filter);
        assertEquals(LOGIN_URL, requestMatcher.getPattern());
    }

    @Test
    void constructorShouldSetAuthenticationSuccessHandler() {
        Field field = ReflectionUtils.findField(TokenResponseUsernamePasswordAuthenticationFilter.class, "successHandler");
        ReflectionUtils.makeAccessible(field);
        AuthenticationSuccessHandler successHandler = (AuthenticationSuccessHandler) ReflectionUtils.getField(field, filter);
        assertEquals(authenticationSuccessHandler, successHandler);
    }

    @Test
    void constructorShouldSetAuthenticationFailureHandler() {
        Field field = ReflectionUtils.findField(TokenResponseUsernamePasswordAuthenticationFilter.class, "failureHandler");
        ReflectionUtils.makeAccessible(field);
        AuthenticationFailureHandler failureHandler = (AuthenticationFailureHandler) ReflectionUtils.getField(field, filter);
        assertEquals(authenticationFailureHandler, failureHandler);
    }

    @Test
    void constructorShouldSetAuthenticationManager() {
        Field field = ReflectionUtils.findField(TokenResponseUsernamePasswordAuthenticationFilter.class, "authenticationManager");
        ReflectionUtils.makeAccessible(field);
        AuthenticationManager authenticationManager = (AuthenticationManager) ReflectionUtils.getField(field, filter);
        assertEquals(authenticationManager, authenticationManager);
    }


}