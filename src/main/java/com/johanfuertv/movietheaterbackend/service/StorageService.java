
package com.johanfuertv.movietheaterbackend.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String store(MultipartFile file, String folder);
    void delete(String fileUrl);
    String getPublicUrl(String fileName);
}
