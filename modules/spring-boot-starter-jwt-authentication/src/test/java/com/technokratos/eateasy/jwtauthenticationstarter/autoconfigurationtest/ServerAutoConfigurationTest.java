package com.technokratos.eateasy.jwtauthenticationstarter.autoconfigurationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.eateasy.jwtauthenticationstarter.SecurityConfig;
import com.technokratos.eateasy.jwtauthenticationstarter.configurer.SecurityConfigurer;
import com.technokratos.eateasy.jwtauthenticationstarter.security.filter.AccessTokenAuthenticationProcessingFilter;
import com.technokratos.eateasy.jwtauthenticationstarter.security.filter.TokenResponseRefreshTokenAuthenticationFilter;
import com.technokratos.eateasy.jwtauthenticationstarter.security.filter.TokenResponseUsernamePasswordAuthenticationFilter;
import com.technokratos.eateasy.jwtauthenticationstarter.security.handler.RefreshTokenInvalidationLogoutHandler;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.configurers.*;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ServerAutoConfigurationTest {

    private static final String[] SERVER_MODE_PROPERTIES = new String[] {
            "jwt.mode=server",
            "jwt.login-url=/api/v1/auth/login",
            "jwt.refresh-url=/api/v1/auth/refresh",
            "jwt.logout-url=/api/v1/auth/logout",
            "jwt.issuer=http://localhost:8080",
            "jwt.tokens.access.header=Authorization",
            "jwt.tokens.access.prefix=Bearer ",
            "jwt.tokens.access.expiration=3m",
            "jwt.tokens.access.key.algorithm=RSA",
            "jwt.tokens.access.key.private-key=classpath:/security/access_private.pem",
            "jwt.tokens.access.key.public-key=classpath:/security/access_public.pem",
            "jwt.tokens.refresh.use-cookie=true",
            "jwt.tokens.refresh.cookie-name=refresh_token",
            "jwt.tokens.refresh.expiration=48h",
            "jwt.tokens.refresh.key.algorithm=RSA",
            "jwt.tokens.refresh.key.private-key=classpath:/security/refresh_private.pem",
            "jwt.tokens.refresh.key.public-key=classpath:/security/refresh_public.pem",
    };

    private static final String[] CLIENT_MODE_PROPERTIES = new String[] {
            "jwt.mode=client",
            "jwt.issuer=http://localhost:8080",
            "jwt.tokens.access.header=Authorization",
            "jwt.tokens.access.prefix=Bearer ",
            "jwt.tokens.access.key.algorithm=RSA",
            "jwt.tokens.access.key.public-key=classpath:/security/access_public.pem",
    };

    private static final String[] OFF_MODE_PROPERTIES = new String[] {
            "jwt.mode=off",
    };

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SecurityConfig.class, UserDetailsConfig.class));

    private final HttpSecurity httpSecurity = mock(HttpSecurity.class);
    private final HttpSecurity configuredHttpSecurity = mock(HttpSecurity.class);
    private final ObjectMapper objectMapper = mock(ObjectMapper.class);
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final AuthenticationManagerBuilder authenticationManagerBuilder = mock(AuthenticationManagerBuilder.class);

    @BeforeEach
    void setUp() throws Exception{
        when(httpSecurity.getSharedObject(AuthenticationManagerBuilder.class))
                .thenReturn(authenticationManagerBuilder);
        when(authenticationManagerBuilder.authenticationProvider(any()))
                .thenReturn(authenticationManagerBuilder);
        when(authenticationManagerBuilder.build()).thenReturn(authenticationManager);
        when(configuredHttpSecurity.csrf(any())).thenReturn(configuredHttpSecurity);
        when(configuredHttpSecurity.sessionManagement(any())).thenReturn(configuredHttpSecurity);
        when(configuredHttpSecurity.addFilterAfter(any(), any())).thenReturn(configuredHttpSecurity);
        when(configuredHttpSecurity.addFilterBefore(any(), any())).thenReturn(configuredHttpSecurity);
        when(configuredHttpSecurity.authorizeHttpRequests(any())).thenReturn(configuredHttpSecurity);
        when(configuredHttpSecurity.httpBasic(any())).thenReturn(configuredHttpSecurity);
        when(configuredHttpSecurity.formLogin(any())).thenReturn(configuredHttpSecurity);
        when(configuredHttpSecurity.logout(any())).thenReturn(configuredHttpSecurity);
    }

    @Test
    void testThatServerModeConfigureCorrectly() {
        contextRunner
                .withPropertyValues(SERVER_MODE_PROPERTIES)
                .withBean(HttpSecurity.class, () -> httpSecurity)
                .withBean(ObjectMapper.class, () -> objectMapper)
                .run(context -> {
                    assertThat(context).hasSingleBean(SecurityConfigurer.class);

                    SecurityConfigurer configurer = context.getBean(SecurityConfigurer.class);
                    configurer.configure(configuredHttpSecurity);

                    verifyThatConfigurerDisableCsrf();
                    verifyThatConfigurerSetStatelessSessionCreationPolicy();
                    verifyThatConfigurerAddLoginFilter();
                    verifyThatConfigurerAddAccessTokenFilterInServerMode();
                    verifyThatConfigurerAddRefreshTokenFilter();
                    verifyThatConfigurerPermitErrorPath();
                    verifyThatConfigurerDisableHttpBasic();
                    verifyThatConfigurerDisableFormLogin();
                    verifyThatConfigurerSetUpLogoutFlow();
                });
    }

    @Test
    void testThatClientModeConfigureCorrectly() {
        contextRunner
                .withPropertyValues(CLIENT_MODE_PROPERTIES)
                .withBean(HttpSecurity.class, () -> httpSecurity)
                .withBean(ObjectMapper.class, () -> objectMapper)
                .run(context -> {
                    assertThat(context).hasSingleBean(SecurityConfigurer.class);

                    SecurityConfigurer configurer = context.getBean(SecurityConfigurer.class);
                    configurer.configure(configuredHttpSecurity);

                    verifyThatConfigurerDisableCsrf();
                    verifyThatConfigurerSetStatelessSessionCreationPolicy();
                    verifyThatConfigurerAddAccessTokenFilterInClientMode();
                    verifyThatConfigurerPermitErrorPath();
                    verifyThatConfigurerDisableHttpBasic();
                    verifyThatConfigurerDisableFormLogin();
                    verifyThatConfigurerDisableLogout();
                });
    }

    @Test
    void testThatOffModeDoNotConfigureAnything() {
        contextRunner
                .withPropertyValues(OFF_MODE_PROPERTIES)
                .withBean(HttpSecurity.class, () -> httpSecurity)
                .withBean(ObjectMapper.class, () -> objectMapper)
                .run(context -> {
                    assertThat(context).hasSingleBean(SecurityConfigurer.class);

                    SecurityConfigurer configurer = context.getBean(SecurityConfigurer.class);

                    configurer.configure(configuredHttpSecurity);
                    verifyNoInteractions(configuredHttpSecurity);

                });
    }

    @SuppressWarnings("unchecked")
    private void verifyThatConfigurerDisableCsrf() throws Exception {
        ArgumentCaptor<Customizer<CsrfConfigurer<HttpSecurity>>> csrfConfigurerCaptor =
                ArgumentCaptor.forClass(Customizer.class);
        verify(configuredHttpSecurity).csrf(csrfConfigurerCaptor.capture());
        var customizer = csrfConfigurerCaptor.getValue();

        CsrfConfigurer<HttpSecurity> csrfConfigurer = mock(CsrfConfigurer.class);
        customizer.customize(csrfConfigurer);
        verify(csrfConfigurer).disable();
    }

    @SuppressWarnings("unchecked")
    private void verifyThatConfigurerSetStatelessSessionCreationPolicy() throws Exception {
        ArgumentCaptor<Customizer<SessionManagementConfigurer<HttpSecurity>>> sessionArgumentCaptor =
                ArgumentCaptor.forClass(Customizer.class);
        verify(configuredHttpSecurity).sessionManagement(sessionArgumentCaptor.capture());
        var customizer = sessionArgumentCaptor.getValue();

        SessionManagementConfigurer<HttpSecurity> sessionManagementConfigurer = mock(SessionManagementConfigurer.class);
        customizer.customize(sessionManagementConfigurer);

        verify(sessionManagementConfigurer).sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @SuppressWarnings("unchecked")
    private void verifyThatConfigurerAddLoginFilter() throws Exception {
        ArgumentCaptor<Filter> filterCaptor = ArgumentCaptor.forClass(Filter.class);
        ArgumentCaptor<Class<? extends Filter>> filterClassCaptor = ArgumentCaptor.forClass(Class.class);
        verify(configuredHttpSecurity, times(2)).addFilterAfter(filterCaptor.capture(), filterClassCaptor.capture());

        Filter filter = filterCaptor.getAllValues().get(0);
        Class<? extends Filter> filterClass = filterClassCaptor.getAllValues().get(0);
        assertThat(filter).isInstanceOf(TokenResponseUsernamePasswordAuthenticationFilter.class);
        assertThat(filterClass).isEqualTo(UsernamePasswordAuthenticationFilter.class);
    }

    @SuppressWarnings("unchecked")
    private void verifyThatConfigurerAddAccessTokenFilterInServerMode() {
        ArgumentCaptor<Filter> filterCaptor = ArgumentCaptor.forClass(Filter.class);
        ArgumentCaptor<Class<? extends Filter>> filterClassCaptor = ArgumentCaptor.forClass(Class.class);
        verify(configuredHttpSecurity, times(2)).addFilterAfter(filterCaptor.capture(), filterClassCaptor.capture());

        Filter filter = filterCaptor.getAllValues().get(1);
        Class<? extends Filter> filterClass = filterClassCaptor.getAllValues().get(1);

        assertThat(filter).isInstanceOf(AccessTokenAuthenticationProcessingFilter.class);
        assertThat(filterClass).isEqualTo(TokenResponseUsernamePasswordAuthenticationFilter.class);
    }

    @SuppressWarnings("unchecked")
    void verifyThatConfigurerAddRefreshTokenFilter() {
        ArgumentCaptor<Filter> filterCaptor = ArgumentCaptor.forClass(Filter.class);
        ArgumentCaptor<Class<? extends Filter>> filterClassCaptor = ArgumentCaptor.forClass(Class.class);
        verify(configuredHttpSecurity, times(1)).addFilterBefore(filterCaptor.capture(), filterClassCaptor.capture());

        Filter filter = filterCaptor.getValue();
        Class<? extends Filter> filterClass = filterClassCaptor.getValue();

        assertThat(filter).isInstanceOf(TokenResponseRefreshTokenAuthenticationFilter.class);
        assertThat(filterClass).isEqualTo(AccessTokenAuthenticationProcessingFilter.class);
    }

    @SuppressWarnings("unchecked")
    void verifyThatConfigurerPermitErrorPath() throws Exception{
        ArgumentCaptor<Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>>
                authorizeHttpRequestsCaptor =
                ArgumentCaptor.forClass(Customizer.class);
        verify(configuredHttpSecurity).authorizeHttpRequests(authorizeHttpRequestsCaptor.capture());
        var customizer = authorizeHttpRequestsCaptor.getValue();

        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry = mock(AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry.class);
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl = mock(AuthorizeHttpRequestsConfigurer.AuthorizedUrl.class);
        when(registry.requestMatchers((String[]) any())).thenReturn(authorizedUrl);
        customizer.customize(registry);

        verify(registry).requestMatchers(eq("/error"));
        verify(authorizedUrl).permitAll();
        verifyNoMoreInteractions(registry, authorizedUrl);
    }

    @SuppressWarnings("unchecked")
    void verifyThatConfigurerDisableHttpBasic() throws Exception {
        ArgumentCaptor<Customizer<HttpBasicConfigurer<HttpSecurity>>> httpBasicConfigurerCaptor =
                ArgumentCaptor.forClass(Customizer.class);

        verify(configuredHttpSecurity).httpBasic(httpBasicConfigurerCaptor.capture());
        var customizer = httpBasicConfigurerCaptor.getValue();
        HttpBasicConfigurer<HttpSecurity> httpBasicConfigurer = mock(HttpBasicConfigurer.class);
        customizer.customize(httpBasicConfigurer);

        verify(httpBasicConfigurer).disable();
    }

    @SuppressWarnings("unchecked")
    void verifyThatConfigurerDisableFormLogin() throws Exception {
        ArgumentCaptor<Customizer<FormLoginConfigurer<HttpSecurity>>> formLoginConfigurerCaptor =
                ArgumentCaptor.forClass(Customizer.class);

        verify(configuredHttpSecurity).formLogin(formLoginConfigurerCaptor.capture());
        var customizer = formLoginConfigurerCaptor.getValue();

        FormLoginConfigurer<HttpSecurity> formLoginConfigurer = mock(FormLoginConfigurer.class);
        customizer.customize(formLoginConfigurer);

        verify(formLoginConfigurer).disable();
    }

    @SuppressWarnings("unchecked")
    void verifyThatConfigurerSetUpLogoutFlow() throws Exception{
        ArgumentCaptor<Customizer<LogoutConfigurer<HttpSecurity>>> logoutConfigurerCaptor =
                ArgumentCaptor.forClass(Customizer.class);

        verify(configuredHttpSecurity).logout(logoutConfigurerCaptor.capture());
        var customizer = logoutConfigurerCaptor.getValue();

        LogoutConfigurer<HttpSecurity> logoutConfigurer = mock(LogoutConfigurer.class);
        when(logoutConfigurer.logoutUrl(any())).thenReturn(logoutConfigurer);
        when(logoutConfigurer.addLogoutHandler(any())).thenReturn(logoutConfigurer);
        when(logoutConfigurer.logoutSuccessHandler(any())).thenReturn(logoutConfigurer);
        customizer.customize(logoutConfigurer);

        verify(logoutConfigurer).logoutUrl(eq("/api/v1/auth/logout"));
        verify(logoutConfigurer).addLogoutHandler(argThat(handler -> handler instanceof RefreshTokenInvalidationLogoutHandler));
        verify(logoutConfigurer).logoutSuccessHandler(any());
        verifyNoMoreInteractions(logoutConfigurer);
    }

    @SuppressWarnings("unchecked")
    private void verifyThatConfigurerAddAccessTokenFilterInClientMode() {
        ArgumentCaptor<Filter> filterCaptor = ArgumentCaptor.forClass(Filter.class);
        ArgumentCaptor<Class<? extends Filter>> filterClassCaptor = ArgumentCaptor.forClass(Class.class);
        verify(configuredHttpSecurity, times(1)).addFilterAfter(filterCaptor.capture(), filterClassCaptor.capture());

        Filter filter = filterCaptor.getValue();
        Class<? extends Filter> filterClass = filterClassCaptor.getValue();

        assertThat(filter).isInstanceOf(AccessTokenAuthenticationProcessingFilter.class);
        assertThat(filterClass).isEqualTo(TokenResponseUsernamePasswordAuthenticationFilter.class);
    }

    @SuppressWarnings("unchecked")
    private void verifyThatConfigurerDisableLogout() throws Exception {
        ArgumentCaptor<Customizer<LogoutConfigurer<HttpSecurity>>> logoutConfigurerCaptor =
                ArgumentCaptor.forClass(Customizer.class);

        verify(configuredHttpSecurity).logout(logoutConfigurerCaptor.capture());
        var customizer = logoutConfigurerCaptor.getValue();

        LogoutConfigurer<HttpSecurity> logoutConfigurer = mock(LogoutConfigurer.class);
        customizer.customize(logoutConfigurer);

        verify(logoutConfigurer).disable();
        verifyNoMoreInteractions(logoutConfigurer);
    }



    @Configuration(proxyBeanMethods = false)
    private static class UserDetailsConfig {

        @Bean
        public UserDetailsService userDetailsService() {
            return new InMemoryUserDetailsManager();
        }
    }
}
