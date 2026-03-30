package com.datalayer.application;

import com.datalayer.db.entity.ArtifactEntity;
import com.datalayer.db.entity.ArtifactType;
import com.datalayer.metadata.ArtifactMetadataStore;
import com.datalayer.storage.StorageRegistry;
import com.datalayer.storage.StorageService;
import com.datalayer.storage.key.StorageKeyGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
public class ArtifactApplicationService {

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream"; // fallback content type
    private static final String DEFAULT_STORAGE_ID = "local-default"; // current storage node
    private static final String UNKNOWN_FILENAME = "unknown-file"; // fallback filename
    private static final String DEFAULT_EXTENSION = "bin"; // fallback extension

    private final StorageRegistry storageRegistry; // resolves storage by id
    private final StorageKeyGenerator storageKeyGenerator; // generates storage keys
    private final ArtifactMetadataStore metadataStore; // metadata abstraction

    public ArtifactApplicationService(StorageRegistry storageRegistry,
                                      StorageKeyGenerator storageKeyGenerator,
                                      ArtifactMetadataStore metadataStore) {
        this.storageRegistry = storageRegistry;
        this.storageKeyGenerator = storageKeyGenerator;
        this.metadataStore = metadataStore;
    }

    public UUID saveArtifact(InputStream inputStream,
                             String originalFilename,
                             String contentType,
                             long size,
                             ArtifactType artifactType) throws IOException {

        UUID artifactId = UUID.randomUUID();
        String storageId = selectStorageId(artifactType, size); // choose storage
        StorageService storage = resolveStorage(storageId);

        String filename = normalizeFilename(originalFilename);
        String type = normalizeContentType(contentType);
        String extension = extractExtension(filename);
        String folder = resolveTypeFolder(artifactType);
        String storageKey = storageKeyGenerator.generate(folder, extension);

        storage.save(storageKey, inputStream);

        ArtifactEntity entity = ArtifactEntity.builder()
                .artifactId(artifactId)
                .storageId(storageId)
                .storageKey(storageKey)
                .originalFilename(filename)
                .contentType(type)
                .size(size)
                .artifactType(artifactType)
                .createdAt(Instant.now())
                .build();

        try {
            metadataStore.save(entity);
        } catch (RuntimeException e) {
            cleanup(storage, storageKey);
            throw e;
        }

        return artifactId;
    }

    @Transactional(readOnly = true)
    public boolean existsByArtifactId(UUID artifactId) {
        return metadataStore.findById(artifactId)
                .map(this::existsInStorage) // check storage
                .orElse(false);
    }

    @Transactional
    public void deleteByArtifactId(UUID artifactId) throws IOException {
        ArtifactEntity entity = metadataStore.findById(artifactId)
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId)); // not found

        StorageService storage = resolveStorage(entity.getStorageId());
        storage.delete(entity.getStorageKey()); // delete file
        metadataStore.delete(entity); // delete metadata
    }

    @Transactional(readOnly = true)
    public ArtifactDownloadPayload loadByArtifactId(UUID artifactId) throws IOException {
        ArtifactEntity entity = metadataStore.findById(artifactId)
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId)); // not found

        StorageService storage = resolveStorage(entity.getStorageId());
        InputStream stream = storage.load(entity.getStorageKey()); // load file

        return new ArtifactDownloadPayload(
                stream,
                entity.getOriginalFilename(),
                entity.getContentType(),
                entity.getSize()
        );
    }

    private boolean existsInStorage(ArtifactEntity entity) {
        StorageService storage = resolveStorage(entity.getStorageId());
        return storage.exists(entity.getStorageKey());
    }

    private StorageService resolveStorage(String storageId) {
        return storageRegistry.getById(storageId); // lookup storage
    }

    private String selectStorageId(ArtifactType artifactType, long size) {
        return DEFAULT_STORAGE_ID; // future routing point
    }

    private String normalizeContentType(String contentType) {
        return Objects.requireNonNullElse(contentType, DEFAULT_CONTENT_TYPE);
    }

    private String normalizeFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return UNKNOWN_FILENAME;
        }
        return originalFilename;
    }

    private String extractExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot == -1 || dot == filename.length() - 1) {
            return DEFAULT_EXTENSION;
        }
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private String resolveTypeFolder(ArtifactType type) {
        return switch (type) {
            case DATASET -> "datasets";
            case MODEL_WEIGHTS -> "models";
        };
    }

    private void cleanup(StorageService storage, String key) {
        try {
            if (storage.exists(key)) {
                storage.delete(key);
            }
        } catch (IOException ignored) {
        }
    }
}