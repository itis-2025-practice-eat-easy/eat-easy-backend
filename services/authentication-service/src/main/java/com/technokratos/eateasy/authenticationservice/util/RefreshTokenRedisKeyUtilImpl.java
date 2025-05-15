package com.technokratos.eateasy.authenticationservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenRedisKeyUtilImpl implements RefreshTokenRedisKeyUtil {

    @Value("${custom.repository.refresh-token.key-prefix}")
    private final String refreshTokenKeyPrefix;

    @Override
    public UUID generateId() {
        return UUID.randomUUID();
    }

    @Override
    public String getKey(UUID id) {
        return refreshTokenKeyPrefix.concat(id.toString());
    }
}
