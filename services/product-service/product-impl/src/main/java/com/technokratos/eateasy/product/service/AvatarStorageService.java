package com.technokratos.eateasy.product.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface AvatarStorageService {
  UUID uploadAvatar(MultipartFile file);
}
