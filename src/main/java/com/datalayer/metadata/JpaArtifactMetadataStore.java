package com.datalayer.metadata;

import com.datalayer.db.entity.ArtifactEntity;
import com.datalayer.db.repository.ArtifactRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class JpaArtifactMetadataStore implements ArtifactMetadataStore {

    private final ArtifactRepository artifactRepository;

    public JpaArtifactMetadataStore(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    @Override
    public ArtifactEntity save(ArtifactEntity entity) {
        return artifactRepository.save(entity);
    }

    @Override
    public Optional<ArtifactEntity> findById(UUID artifactId) {
        return artifactRepository.findById(artifactId);
    }

    @Override
    public void delete(ArtifactEntity entity) {
        artifactRepository.delete(entity);
    }
}