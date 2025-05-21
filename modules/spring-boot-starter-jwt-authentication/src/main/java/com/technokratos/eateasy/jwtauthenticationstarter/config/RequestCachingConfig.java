package com.technokratos.eateasy.jwtauthenticationstarter.config;

import com.technokratos.eateasy.jwtauthenticationstarter.security.filter.HttpServletRequestBodyCachingFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@ConditionalOnProperty(prefix = "jwt", name = "mode", havingValue = "server")
public class RequestCachingConfig {

    private static final int REQUEST_CACHING_FILTER_ORDER = -106;

    @Bean
    public HttpServletRequestBodyCachingFilter httpServletRequestBodyCachingFilter() {
        return new HttpServletRequestBodyCachingFilter();
    }

    @Bean
    public FilterRegistrationBean<HttpServletRequestBodyCachingFilter> httpServletRequestBodyCachingFilterRegistrationBean() {
        FilterRegistrationBean<HttpServletRequestBodyCachingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(httpServletRequestBodyCachingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(REQUEST_CACHING_FILTER_ORDER);
        return registrationBean;
    }
}
