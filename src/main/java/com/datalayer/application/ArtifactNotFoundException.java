package com.datalayer.application;

import java.util.UUID;

public class ArtifactNotFoundException extends RuntimeException {

    public ArtifactNotFoundException(UUID artifactId) {
        super("artifact not found: " + artifactId);
    }
}