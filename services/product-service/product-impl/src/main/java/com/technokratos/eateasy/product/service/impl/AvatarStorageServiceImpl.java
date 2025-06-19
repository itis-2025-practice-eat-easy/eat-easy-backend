package com.technokratos.eateasy.product.service.impl;

import com.technokratos.eateasy.product.entity.ImageReference;
import com.technokratos.eateasy.product.repository.ImageReferenceRepository;
import com.technokratos.eateasy.product.service.AvatarStorageService;
import com.technokratos.eateasy.product.util.MinioProperties;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvatarStorageServiceImpl implements AvatarStorageService {
  private final MinioClient minioClient;
  private final MinioProperties properties;
  private final ImageReferenceRepository repository;

  @Override
  public UUID uploadAvatar(MultipartFile file) {
    try {
      log.info("Start uploading avatar for product");

      String objectName = generateObjectName(file);
      log.info("Generated object name: {}", objectName);

      ensureBucketExistsWithPolicy();
      log.info("Bucket exists or created with policy");

      uploadToMinio(file, objectName);
      log.info("File uploaded to MinIO: {}", objectName);

      String originalFilename = file.getOriginalFilename();
      if (originalFilename == null || originalFilename.isBlank()) {
        throw new IllegalArgumentException("File name could not be blank");
      }

      String fileNameWithoutExt = originalFilename.contains(".")
              ? originalFilename.substring(0, originalFilename.lastIndexOf('.'))
              : originalFilename;
      String extension = originalFilename.contains(".")
              ? originalFilename.substring(originalFilename.lastIndexOf('.') + 1)
              : "";

      UUID photoId = UUID.randomUUID();
      ImageReference imageReference = ImageReference.builder()
              .id(photoId)
              .baseUrl(properties.getUrl())
              .bucketName(properties.getBucketName())
              .folderName(properties.getAvatarFolder())
              .fileName(fileNameWithoutExt)
              .extension(extension)
              .build();

      log.info("Saving ImageReference to DB: {}", imageReference);
      repository.save(imageReference);
      log.info("Avatar metadata saved with ID: {}", photoId);

      return photoId;

    } catch (Exception e) {
      log.error("Avatar load exception: {}", e.getMessage(), e);
      throw new RuntimeException("Avatar load exception", e);
    }
  }


  private String generateObjectName(MultipartFile file) {
    return properties.getAvatarFolder() + "/" + file.getOriginalFilename();
  }

  private void ensureBucketExistsWithPolicy() throws Exception {
    String bucket = properties.getBucketName();
    if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
      String policyJson = properties.getBucketPolicyTemplate().formatted(bucket);
      minioClient.setBucketPolicy(
              SetBucketPolicyArgs.builder().bucket(bucket).config(policyJson).build());
    }
  }

  private void uploadToMinio(MultipartFile file, String objectName) throws Exception {
    minioClient.putObject(
            PutObjectArgs.builder()
                    .bucket(properties.getBucketName())
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
  }
}
