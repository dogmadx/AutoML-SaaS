package com.datalayer.storage;

public interface StorageRegistry {
    StorageService getById(String storageId);
}