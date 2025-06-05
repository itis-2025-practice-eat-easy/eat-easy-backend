package com.technokratos.eateasy.jwtauthenticationstarter.utils.requestmapper.impl;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JacksonObjectMapperRequestMapperTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private ServletInputStream inputStream;
    @Mock
    private ObjectMapper objectMapper;

    private JacksonObjectMapperRequestMapper requestMapper;

    @BeforeEach
    void setup() {
        requestMapper = new JacksonObjectMapperRequestMapper(objectMapper);
    }

    @Test
    void getObjectFromRequestForValidRequestShouldReturnObject() throws Exception {
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(inputStream, TestClass.class)).thenReturn(new TestClass());

        assertNotNull(requestMapper.getObjectFromRequest(request, TestClass.class));
    }

    @Test
    void getObjectFromRequestForDatabindExceptionShouldReturnNull() throws Exception {
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(inputStream, TestClass.class))
                .thenThrow(mock(DatabindException.class));

        assertNull(requestMapper.getObjectFromRequest(request, TestClass.class));
    }

    @Test
    void getObjectFromRequestForIOExceptionShouldReturnNull() throws Exception {
        when(request.getInputStream()).thenThrow(new IOException());
        assertNull(requestMapper.getObjectFromRequest(request, TestClass.class));
    }

    private static class TestClass {}
}