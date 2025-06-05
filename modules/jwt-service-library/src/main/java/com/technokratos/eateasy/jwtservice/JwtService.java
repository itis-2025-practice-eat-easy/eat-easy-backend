package com.technokratos.eateasy.jwtservice;

/**
 * Comprehensive service interface combining JWT generation, parsing, and validation operations.
 * <p>
 * Inherits functionality from both {@link JwtGeneratorService} and {@link JwtParserService}.
 * </p>
 */
public interface JwtService extends JwtGeneratorService, JwtParserService{
}
