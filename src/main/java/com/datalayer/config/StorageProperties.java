package com.datalayer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StorageProperties {
    @Value("${storage.root-path}")
    private String rootPath;

    public String getRootPath() {
        return rootPath;
    }
}
