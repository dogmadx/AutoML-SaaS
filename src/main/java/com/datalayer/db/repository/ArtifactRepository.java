package com.datalayer.db.repository;

import com.datalayer.db.entity.ArtifactEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtifactRepository extends JpaRepository<ArtifactEntity, UUID> {
}
