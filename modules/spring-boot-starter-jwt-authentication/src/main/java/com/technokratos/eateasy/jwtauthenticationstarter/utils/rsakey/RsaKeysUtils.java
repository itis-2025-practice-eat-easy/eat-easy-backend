package com.technokratos.eateasy.jwtauthenticationstarter.utils.rsakey;

import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class RsaKeysUtils {

    private static final String algorithm = "RSA";
    private RsaKeysUtils() {
        throw new IllegalStateException("Utility class");
    }


    public static PrivateKey loadPrivateKey(Resource resource)
            throws IOException, GeneralSecurityException {

        String pem = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8)
                .replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\\s", "");

        byte[] der = Base64.getDecoder().decode(pem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
        return KeyFactory.getInstance(algorithm).generatePrivate(spec);
    }

    public static PublicKey loadPublicKey(Resource resource)
            throws IOException, GeneralSecurityException {

        String pem = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8)
                .replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\\s", "");

        byte[] der = Base64.getDecoder().decode(pem);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
        return KeyFactory.getInstance(algorithm).generatePublic(spec);
    }
}
