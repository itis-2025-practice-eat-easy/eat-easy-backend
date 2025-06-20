package com.technokratos.eateasy.product.util;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
  private String url;
  private String accessKey;
  private String secretKey;
  private String bucketName;
  private String avatarFolder;
  private String bucketPolicyTemplate;
}
