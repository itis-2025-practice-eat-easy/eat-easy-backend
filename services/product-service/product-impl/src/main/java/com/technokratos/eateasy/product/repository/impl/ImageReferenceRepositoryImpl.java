package com.technokratos.eateasy.product.repository.impl;

import com.technokratos.eateasy.product.entity.ImageReference;
import com.technokratos.eateasy.product.repository.ImageReferenceRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class ImageReferenceRepositoryImpl implements ImageReferenceRepository {

    private final JdbcTemplate jdbcTemplate;

    private final static String SAVE_SQL = """
            INSERT INTO image_reference (id, base_url, bucket_name, folder_name, file_name, extension)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
    private final static String GET_BY_ID_SQL = "SELECT * FROM image_reference WHERE id = ?";

    private final RowMapper<ImageReference> rowMapper = (rs, rowNum) -> ImageReference.builder()
            .id(UUID.fromString(rs.getString("id")))
            .baseUrl(rs.getString("base_url"))
            .bucketName(rs.getString("bucket_name"))
            .folderName(rs.getString("folder_name"))
            .fileName(rs.getString("file_name"))
            .extension(rs.getString("extension"))
            .build();

    @Override
    public void save(ImageReference imageReference) {

        jdbcTemplate.update(SAVE_SQL,
                imageReference.getId(),
                imageReference.getBaseUrl(),
                imageReference.getBucketName(),
                imageReference.getFolderName(),
                imageReference.getFileName(),
                imageReference.getExtension()
        );
    }
    @Override
    public Optional<ImageReference> findById(UUID id) {
        ImageReference imageReference = jdbcTemplate.queryForObject(GET_BY_ID_SQL, rowMapper, id.toString());
        return Optional.of(imageReference);
    }
}
