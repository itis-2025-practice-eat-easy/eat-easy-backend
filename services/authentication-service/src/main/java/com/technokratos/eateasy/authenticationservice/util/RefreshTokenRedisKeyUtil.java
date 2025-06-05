package com.technokratos.eateasy.authenticationservice.util;

import java.util.UUID;

public interface RefreshTokenRedisKeyUtil {

    UUID generateId();
    String getKey(UUID id);
}
