package com.technokratos.eateasy.product.repository;

import com.technokratos.eateasy.product.entity.ImageReference;

import java.util.Optional;
import java.util.UUID;

public interface ImageReferenceRepository {
    void save(ImageReference imageReference);
    Optional<ImageReference> findById(UUID id);
}
