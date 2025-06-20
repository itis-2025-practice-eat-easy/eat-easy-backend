package com.technokratos.eateasy.jwtauthenticationstarter.utils.requestmapper;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

/**
 * Interface for deserializing HTTP request bodies to Java objects.
 */
public interface RequestMapper {

    /**
     * Converts HTTP request body to specified Java type.
     *
     * @param request      HTTP servlet request
     * @param requestClass target type for deserialization
     * @param <T>          generic return type
     * @return deserialized object or {@code null} on failure
     */
    @Nullable
    <T> T getObjectFromRequest(HttpServletRequest request, Class<T> requestClass);
}
