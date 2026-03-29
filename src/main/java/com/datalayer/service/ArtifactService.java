package com.datalayer.service;

import com.datalayer.db.entity.ArtifactEntity;
import com.datalayer.db.entity.ArtifactType;
import com.datalayer.db.repository.ArtifactRepository;
import com.datalayer.storage.StorageService;
import com.datalayer.storage.key.StorageKeyGenerator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;
import java.util.Objects;

@Service
public class ArtifactService {

    public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final StorageService storageService;
    private final StorageKeyGenerator storageKeyGenerator;
    private final ArtifactRepository artifactRepository;

    public ArtifactService(StorageService storageService, StorageKeyGenerator storageKeyGenerator, ArtifactRepository artifactRepository) {
        this.storageService = storageService;
        this.storageKeyGenerator = storageKeyGenerator;
        this.artifactRepository = artifactRepository;
    }

    /**
     * save dataset:
     * 1. generate outer artifactId
     * 2. generate inner storageKey
     * 3. save file to storage
     * 4. save metadata to Postgres
     *
     * return safe artifact id
     */
    public UUID saveDataset(MultipartFile file) throws IOException {
        UUID artifactId = UUID.randomUUID();
        String extension = extractExtension(file.getOriginalFilename());
        String storageKey = storageKeyGenerator.generate("datasets", extension);

        String originalFilename = safeFilename(file.getOriginalFilename());
        String contentType = resolveContentType(file);

        try (InputStream inputStream = file.getInputStream()) {
            storageService.save(storageKey, inputStream);
        }

        ArtifactEntity entity = new ArtifactEntity().builder()
                .artifactId(artifactId)
                .storageKey(storageKey)
                .originalFilename(originalFilename)
                .contentType(contentType)
                .size(file.getSize())
                .artifactType(ArtifactType.DATASET)
                .createdAt(Instant.now())
                .build();

        try {
            artifactRepository.save(entity);
        } catch (RuntimeException e) {
            safeDeleteFromStorage(storageKey);
            throw e;
        }

        return artifactId;
    }

    @Transactional
    public boolean existsByArtifactId(UUID artifactId) {
        return artifactRepository.findById(artifactId)
                .map(entity -> storageService.exists(entity.getStorageKey()))
                .orElse(false);
    }

    @Transactional
    public void deleteByArtifactId(UUID artifactId) throws IOException {
        ArtifactEntity entity = artifactRepository.findById(artifactId)
                .orElseThrow(() -> new IllegalArgumentException("Artifact not found"));

        storageService.delete(entity.getStorageKey());
        artifactRepository.delete(entity);
    }

    private String resolveContentType(MultipartFile file) {
        return Objects.requireNonNullElse(file.getContentType(), DEFAULT_CONTENT_TYPE);
    }

    /**
     * возвращает безопасное имя файла для metadata
     */
    private String safeFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "unknown-file";
        }
        return originalFilename;
    }

    private void safeDeleteFromStorage(String storageKey) {
        try {
            if (storageService.exists(storageKey)) {
                storageService.delete(storageKey);
            }
        } catch (IOException ignored) {
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "bin";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

}
