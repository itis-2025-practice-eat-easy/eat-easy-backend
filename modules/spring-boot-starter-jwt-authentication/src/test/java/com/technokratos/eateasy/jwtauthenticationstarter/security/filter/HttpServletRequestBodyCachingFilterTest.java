package com.technokratos.eateasy.jwtauthenticationstarter.security.filter;

import com.technokratos.eateasy.jwtauthenticationstarter.utils.requestwrapper.CachedBodyHttpServletRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HttpServletRequestBodyCachingFilterTest {

    private final HttpServletRequestBodyCachingFilter filter = new HttpServletRequestBodyCachingFilter();
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private FilterChain mockFilterChain;

    @Test
    void doFilterInternalShouldWrapRequestWithCachedBodyWrapper() throws Exception {
        filter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain).doFilter(any(CachedBodyHttpServletRequestWrapper.class), any());
    }
}