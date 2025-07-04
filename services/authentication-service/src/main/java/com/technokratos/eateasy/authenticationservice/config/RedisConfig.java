package com.technokratos.eateasy.authenticationservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technokratos.eateasy.jwtauthenticationstarter.token.refresh.model.RefreshTokenEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public RedisTemplate<String, RefreshTokenEntity> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RefreshTokenEntity> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, RefreshTokenEntity.class));

        return redisTemplate;
    }
}
