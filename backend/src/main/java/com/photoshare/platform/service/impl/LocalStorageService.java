package com.photoshare.platform.service.impl;

import com.photoshare.platform.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    private final Path fileStorageLocation;

    public LocalStorageService(@Value("${app.storage.local.upload-dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            log.error("Could not create local upload directory", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subFolder) throws IOException {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "photo.jpg");
        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i);
        }

        String uniqueFileName = UUID.randomUUID() + extension;
        Path targetDir = this.fileStorageLocation.resolve(subFolder);
        Files.createDirectories(targetDir);

        Path targetLocation = targetDir.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + subFolder + "/" + uniqueFileName;
    }

    @Override
    public void deleteFile(String storageLocation) {
        if (storageLocation == null || !storageLocation.startsWith("/uploads/")) {
            return;
        }
        try {
            String relativePath = storageLocation.substring("/uploads/".length());
            Path filePath = this.fileStorageLocation.resolve(relativePath).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Error deleting local file: {}", storageLocation, e);
        }
    }
}
