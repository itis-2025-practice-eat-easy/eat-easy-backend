package com.technokratos.eateasy.jwtauthenticationstarter.configurer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Functional interface for customizing HTTP security configuration.
 * <p>
 * Implementations define security rules through the {@link HttpSecurity} API,
 * allowing customization of authentication, authorization, and other security aspects.
 *
 * @see HttpSecurity
 */
@FunctionalInterface
public interface SecurityConfigurer {

    /**
     * Applies security configurations to the HTTP security builder.
     *
     * @param http HTTP security configuration builder
     */
    void configure(HttpSecurity http) throws Exception;
}
