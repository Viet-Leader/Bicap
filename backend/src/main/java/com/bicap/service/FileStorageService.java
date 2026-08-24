package com.bicap.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storeProductImage(MultipartFile file);

    void delete(String imageUrl);

}
