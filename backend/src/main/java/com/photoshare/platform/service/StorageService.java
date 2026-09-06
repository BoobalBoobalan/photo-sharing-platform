package com.photoshare.platform.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    /**
     * Uploads a file and returns its storage location (URL or key).
     */
    String storeFile(MultipartFile file, String subFolder) throws IOException;

    /**
     * Deletes a file given its storage location or key.
     */
    void deleteFile(String storageLocation);
}
