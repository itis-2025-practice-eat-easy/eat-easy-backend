package com.technokratos.eateasy.jwtauthenticationstarter.utils.requestwrapper;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.io.Reader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachedBodyHttpServletRequestWrapperTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private ServletInputStream inputStream;

    private byte[] bytes = new byte[] {1, 2, 3, 4, 5};

    private CachedBodyHttpServletRequestWrapper wrapper;

    @BeforeEach
    void setUp() throws Exception {
        wrapper = new CachedBodyHttpServletRequestWrapper(request);

        when(request.getInputStream()).thenReturn(inputStream);
        when(inputStream.readAllBytes()).thenReturn(bytes);
    }

    @Test
    void getInputStreamShouldReadBodyOnce() throws Exception {
        InputStream firstStream = wrapper.getInputStream();
        InputStream secondStream = wrapper.getInputStream();

        assertNotNull(firstStream);
        assertNotNull(secondStream);
        assertNotSame(firstStream, secondStream, "Input streams should not be the same instance");

        verify(request, times(1)).getInputStream();
        verify(inputStream, times(1)).readAllBytes();
    }

    @Test
    void getReaderShouldReadBodyOnce() throws Exception {
        Reader firstReader = wrapper.getReader();
        Reader secondReader = wrapper.getReader();

        assertNotNull(firstReader);
        assertNotNull(secondReader);
        assertNotSame(firstReader, secondReader, "Input streams should not be the same instance");

        verify(request, times(1)).getInputStream();
        verify(inputStream, times(1)).readAllBytes();
    }

    @Test
    void getInputStreamShouldReturnCachedBody() throws Exception {
        wrapper.getInputStream();

        InputStream cachedStream = wrapper.getInputStream();

        assertNotNull(cachedStream);
        assertArrayEquals(bytes, cachedStream.readAllBytes());

        verify(request, times(1)).getInputStream();
        verify(inputStream, times(1)).readAllBytes();
    }

    @Test
    void getInputStreamShouldReturnCorrectServletInputStream() throws Exception {
        ServletInputStream servletInputStream = wrapper.getInputStream();
        assertNotNull(servletInputStream);
        assertTrue(servletInputStream.isReady());
        assertFalse(servletInputStream.isFinished());
        assertThrows(UnsupportedOperationException.class, () -> servletInputStream.setReadListener(null));
    }
}