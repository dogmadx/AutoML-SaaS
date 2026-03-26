package com.datalayer.storage.local;

import com.datalayer.config.StorageProperties;
import com.datalayer.storage.StorageService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class LocalFileStorageService implements StorageService {
    private final Path rootPath;

    LocalFileStorageService(StorageProperties storageProperties) {
        this.rootPath = Path.of(storageProperties.getRootPath()).toAbsolutePath().normalize();
    }

    @Override
    public void save(String key, InputStream inputStream) throws IOException {
        Path filePath = resolvePath(key);

        Files.createDirectories(filePath.getParent());
        Files.copy(inputStream, filePath);
    }

    @Override
    public InputStream load(String key) throws IOException {
        Path filePath = resolvePath(key);
        return Files.newInputStream(filePath);
    }

    @Override
    public void delete(String key) throws IOException {
        Path filePath = resolvePath(key);
        Files.deleteIfExists(filePath);
    }

    @Override
    public boolean exists(String key) {
        Path filePath = resolvePath(key);
        return Files.exists(filePath);
    }


    private Path resolvePath(String key) {
        Path path = rootPath.resolve(key).normalize();

        if (!path.startsWith(rootPath)) {
            throw new IllegalArgumentException("invalid storage key");
        }

        return path;
    }

}
