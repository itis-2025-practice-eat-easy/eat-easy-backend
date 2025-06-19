package com.technokratos.eateasy.product.entity;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageReference {
    private UUID id;
    private String baseUrl;
    private String bucketName;
    private String folderName;
    private String fileName;
    private String extension;
}
