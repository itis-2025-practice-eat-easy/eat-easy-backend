package com.technokratos.eateasy.jwtauthenticationstarter.dto.response;

import lombok.Builder;

/**
 * Authentication response containing generated JWT tokens.
 */
@Builder
public record TokenResponse(String access, String refresh) {
    @Override
    public String toString() {
        return "TokenResponse{access='%s', refresh='%s'}".formatted(access, refresh);
    }
}
