package com.datalayer.api.controller;

import com.datalayer.application.ArtifactApplicationService;
import com.datalayer.application.ArtifactDownloadPayload;
import com.datalayer.db.entity.ArtifactType;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController // rest api for artifacts
@RequestMapping("/artifacts")
public class ArtifactController {

    private final ArtifactApplicationService service; // orchestration layer

    public ArtifactController(ArtifactApplicationService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<UUID> upload(@RequestParam("file") MultipartFile file) throws IOException {
        UUID id = service.saveArtifact(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                ArtifactType.DATASET
        );

        return ResponseEntity.ok(id);
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(@RequestParam("artifactId") UUID artifactId) {
        return ResponseEntity.ok(service.existsByArtifactId(artifactId));
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestParam("artifactId") UUID artifactId) throws IOException {
        service.deleteByArtifactId(artifactId);
        return ResponseEntity.ok("deleted");
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download(@RequestParam("artifactId") UUID artifactId) throws IOException {
        ArtifactDownloadPayload payload = service.loadByArtifactId(artifactId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + payload.originalFilename() + "\"") // force download
                .contentType(MediaType.parseMediaType(payload.contentType()))
                .contentLength(payload.size())
                .body(new InputStreamResource(payload.inputStream())); // stream response
    }
}
