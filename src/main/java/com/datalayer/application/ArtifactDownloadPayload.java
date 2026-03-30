package com.datalayer.application;

import java.io.InputStream;

public record ArtifactDownloadPayload(
        InputStream inputStream,
        String originalFilename,
        String contentType,
        long size
) {
}