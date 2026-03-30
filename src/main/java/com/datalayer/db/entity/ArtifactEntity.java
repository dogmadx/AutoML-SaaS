package com.datalayer.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "artifacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtifactEntity {

    @Id
    private UUID artifactId;

    @Column(nullable = false)
    private String storageId;

    @Column(nullable = false, unique = true)
    private String storageKey;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long size;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArtifactType artifactType;

    @Column(nullable = false)
    private Instant createdAt;
}