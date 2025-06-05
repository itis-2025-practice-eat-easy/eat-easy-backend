package com.technokratos.eateasy.jwtauthenticationstarter.token.access.service;

/**
 * Unified service interface for JWT access token lifecycle management.
 * <p>
 * Combines {@link AccessTokenGeneratorService} and {@link AccessTokenParserService}
 * functionality for complete token handling.
 * </p>
 */
public interface AccessTokenService extends AccessTokenGeneratorService, AccessTokenParserService {
}