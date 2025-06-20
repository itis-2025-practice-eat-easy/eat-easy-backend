package com.technokratos.eateasy.jwtauthenticationstarter.utils.requestwrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Wraps an {@link HttpServletRequest} to cache its body for multiple reads.
 * <p>
 * This is particularly useful in scenarios where the request payload needs to be inspected
 * multiple times (e.g., authentication processing, logging, or validation). The body is read
 * and cached on the first access of {@link #getInputStream()} or {@link #getReader()}, and
 * subsequent accesses use the cached data.
 * </p>
 * @see HttpServletRequestWrapper
 */
public class CachedBodyHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private byte[] cachedBody;
    private volatile boolean isCached = false;

    public CachedBodyHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (!isCached) {
            cacheBody();
        }

        return new CachedBodyServletInputStream(new ByteArrayInputStream(cachedBody));
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (!isCached) {
            cacheBody();
        }

        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(cachedBody)));
    }

    private synchronized void cacheBody() throws IOException {
        if (!isCached) {
            InputStream inputStream = super.getInputStream();
            cachedBody = inputStream.readAllBytes();
            isCached = true;
        }
    }

    /**
     * {@link ServletInputStream} implementation that reads from a pre-cached byte array.
     */
    protected static class CachedBodyServletInputStream extends ServletInputStream {
        private final InputStream inputStream;

        public CachedBodyServletInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public boolean isFinished() {
            try {
                return inputStream.available() == 0;
            } catch (IOException e) {
                return true;
            }
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException("ReadListener is not supported");
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }
}
