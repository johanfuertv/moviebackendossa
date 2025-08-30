package com.johanfuertv.movietheaterbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {
    
    private static final Logger logger = LoggerFactory.getLogger(LocalStorageService.class);
    
    @Value("${storage.upload-dir}")
    private String uploadDir;
    
    @Override
    public String store(MultipartFile file, String folder) {
        try {
            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir, folder);
            Files.createDirectories(uploadPath);
            
            // Generate unique filename
            String fileName = generateFileName(file.getOriginalFilename());
            Path filePath = uploadPath.resolve(fileName);
            
            // Copy file to target location
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            String publicUrl = getPublicUrl(folder + "/" + fileName);
            logger.info("File uploaded locally: {}", publicUrl);
            return publicUrl;
            
        } catch (IOException e) {
            logger.error("Error uploading file locally", e);
            throw new RuntimeException("Error uploading file locally", e);
        }
    }
    
    @Override
    public void delete(String fileUrl) {
        try {
            String relativePath = extractRelativePathFromUrl(fileUrl);
            if (relativePath != null) {
                Path filePath = Paths.get(uploadDir, relativePath);
                Files.deleteIfExists(filePath);
                logger.info("File deleted locally: {}", fileUrl);
            }
        } catch (IOException e) {
            logger.error("Error deleting file locally: {}", fileUrl, e);
        }
    }
    
    @Override
    public String getPublicUrl(String fileName) {
        return "/uploads/" + fileName;
    }
    
    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
    
    private String extractRelativePathFromUrl(String url) {
        if (url != null && url.startsWith("/uploads/")) {
            return url.substring(9); // Remove "/uploads/" prefix
        }
        return null;
    }
}
