package com.technokratos.eateasy.jwtservice;

import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

/**
 * Service interface for JWT token generation operations.
 */
public interface JwtGeneratorService {

    /**
     * Generates a JWT token for authenticated user with additional claims.
     *
     * @param userDetails authenticated user details
     * @param extraClaims additional claims to include in the token (must not be null)
     * @return generated JWT token as compact string
     */
    String generate(UserDetails userDetails, @NonNull Map<String, Object> extraClaims);
}
