package com.datalayer.storage.key;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class StorageKeyGenerator {
    public String generate(String type, String extension) {
        LocalDate localDate = LocalDate.now();
        String uuid = UUID.randomUUID().toString();

        return type + "/"
                + localDate.getYear() + "/"
                + String.format("%02d", localDate.getMonthValue()) + "/"
                + String.format("%02d", localDate.getDayOfMonth()) + "/"
                + uuid + "." + extension;
    }
}
