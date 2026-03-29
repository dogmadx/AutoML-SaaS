package com.datalayer.api.controller;

import com.datalayer.service.ArtifactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/artifacts")
public class ArtifactController {

    private final ArtifactService artifactService;

    public ArtifactController(ArtifactService artifactService) {
        this.artifactService = artifactService;
    }

    @PostMapping("/upload")
    public ResponseEntity<UUID> uploadDataset(@RequestParam("file") MultipartFile file) throws IOException {
        UUID artifactId = artifactService.saveDataset(file);
        return ResponseEntity.ok(artifactId);
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(@RequestParam("artifactId") UUID artifactId) {
        return ResponseEntity.ok(artifactService.existsByArtifactId(artifactId));
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestParam("artifactId") UUID artifactId) throws IOException {
        artifactService.deleteByArtifactId(artifactId);
        return ResponseEntity.ok("Deleted");
    }
}