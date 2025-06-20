package com.technokratos.eateasy.common.internalkeyvalidator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InternalKeyValidator {

    private final String secretKey;
    private final String headerName;

    public boolean hasValidKey(HttpServletRequest request) {
        String header = request.getHeader(headerName);
        return secretKey.equals(header);
    }
}