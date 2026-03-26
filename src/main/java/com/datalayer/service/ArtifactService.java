package com.datalayer.service;

import com.datalayer.storage.StorageService;
import com.datalayer.storage.key.StorageKeyGenerator;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ArtifactService {

    private final StorageService storageService;
    private final StorageKeyGenerator storageKeyGenerator;

    public ArtifactService(StorageService storageService, StorageKeyGenerator storageKeyGenerator) {
        this.storageService = storageService;
        this.storageKeyGenerator = storageKeyGenerator;
    }

    public String saveDataset(InputStream inputStream) throws IOException {
        String key = storageKeyGenerator.generate("datasets", "csv");
        storageService.save(key, inputStream);

        return key;
    }

    public InputStream loadByKey(String key) throws IOException {
        return storageService.load(key);
    }

    public void deleteByKey(String key) throws IOException {
        storageService.delete(key);
    }

    public boolean existsByKey(String key) {
        return storageService.exists(key);
    }



}
