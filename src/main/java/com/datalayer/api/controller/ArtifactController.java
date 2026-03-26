package com.datalayer.api.controller;

import com.datalayer.service.ArtifactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/artifacts")
public class ArtifactController {
    private final ArtifactService artifactService;

    ArtifactController(ArtifactService artifactService) {
        this.artifactService = artifactService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDataset(@RequestParam("file") MultipartFile file) throws IOException {
        String key = artifactService.saveDataset(file.getInputStream());
        return ResponseEntity.ok(key);
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(@RequestParam("key") String key) throws IOException {
        return ResponseEntity.ok(artifactService.existsByKey(key));
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestParam("key") String key) throws IOException {
        artifactService.deleteByKey(key);
        return ResponseEntity.ok("Deleted");
    }
}
