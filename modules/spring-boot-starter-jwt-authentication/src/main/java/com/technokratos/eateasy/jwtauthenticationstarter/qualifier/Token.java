package com.technokratos.eateasy.jwtauthenticationstarter.qualifier;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Qualifier annotation for distinguishing between different token types in dependency injection contexts.
 * <p>
 * Used to identify specific token service implementations when multiple candidates exist.
 * </p>
 * @see Qualifier
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
public @interface Token {
    /**
     * Specifies the type of token being qualified
     */
    TokenType value();

    /**
     * Token type classification for authentication system components
     */
    enum TokenType {
        /**
         * Short-lived authorization token with access privileges
         */
        ACCESS,

        /**
         * Long-lived token for obtaining new access tokens
         */
        REFRESH
    }
}
