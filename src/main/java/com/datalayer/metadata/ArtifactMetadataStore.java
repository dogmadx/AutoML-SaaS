package com.datalayer.metadata;

import com.datalayer.db.entity.ArtifactEntity;

import java.util.Optional;
import java.util.UUID;

public interface ArtifactMetadataStore {
    ArtifactEntity save(ArtifactEntity entity);
    Optional<ArtifactEntity> findById(UUID artifactId);
    void delete(ArtifactEntity entity);
}