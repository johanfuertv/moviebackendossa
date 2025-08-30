package com.johanfuertv.movietheaterbackend.config;

import com.johanfuertv.movietheaterbackend.service.LocalStorageService;
import com.johanfuertv.movietheaterbackend.service.S3StorageService;
import com.johanfuertv.movietheaterbackend.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class StorageConfig {
    
    @Value("${storage.type:local}")
    private String storageType;
    
    @Value("${aws.s3.access-key:}")
    private String awsAccessKey;
    
    @Value("${aws.s3.secret-key:}")
    private String awsSecretKey;
    
    @Bean
    public StorageService storageService(S3StorageService s3StorageService, LocalStorageService localStorageService) {
        // Use S3 if credentials are available and storage type is s3
        if ("s3".equalsIgnoreCase(storageType) && 
            StringUtils.hasText(awsAccessKey) && 
            StringUtils.hasText(awsSecretKey)) {
            return s3StorageService;
        } else {
            return localStorageService;
        }
    }
}