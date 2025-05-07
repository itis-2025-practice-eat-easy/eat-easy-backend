package com.technokratos.eateasy.jwtauthenticationstarter.utils.requestmapper.impl;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.eateasy.jwtauthenticationstarter.utils.requestmapper.RequestMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Jackson-based implementation of {@link RequestMapper} using {@link ObjectMapper}.
 * <p>
 * Handles JSON parsing errors and input/output exceptions gracefully, returning {@code null}
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
public class JacksonObjectMapperRequestMapper implements RequestMapper {

    private final ObjectMapper objectMapper;

    @Override
    public <T> T getObjectFromRequest(HttpServletRequest request, Class<T> requestClass) {
        try {
            return objectMapper.readValue(request.getInputStream(), requestClass);
        } catch (DatabindException e) {
            log.debug("Failed to parse request body", e);
        } catch (IOException e) {
            log.debug("Failed to read request body", e);
        }
        return null;
    }
}
