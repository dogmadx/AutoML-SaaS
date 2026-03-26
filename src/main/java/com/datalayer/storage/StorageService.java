package com.datalayer.storage;

import java.io.IOException;
import java.io.InputStream;

public interface StorageService {
    void save(String key, InputStream inp) throws IOException;
    void delete(String key) throws IOException;
    boolean exists(String key);
    InputStream load(String key) throws IOException;
}
