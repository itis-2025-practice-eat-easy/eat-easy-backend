package com.technokratos.eateasy.product.config;
import com.technokratos.eateasy.product.util.MinioProperties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(MinioProperties.class)
@RequiredArgsConstructor
public class MinioConfig {
  private final MinioProperties properties;
  @Bean
  public MinioClient minioClient() {
    return MinioClient.builder()
        .endpoint(properties.getUrl())
        .credentials(properties.getAccessKey(), properties.getSecretKey())
        .build();
  }
}
