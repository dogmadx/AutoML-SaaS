package com.datalayer.storage;

import org.springframework.stereotype.Component;

@Component
public class DefaultStorageRegistry implements StorageRegistry {

    private static final String LOCAL_DEFAULT = "local-default";

    private final StorageService storageService;

    public DefaultStorageRegistry(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public StorageService getById(String storageId) {
        if (!LOCAL_DEFAULT.equals(storageId)) {
            throw new IllegalArgumentException("Unknown storageId: " + storageId);
        }
        return storageService;
    }
}