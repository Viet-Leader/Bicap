package com.bicap.service.impl;

import com.bicap.config.FileStorageProperties;
import com.bicap.exception.BadRequestException;
import com.bicap.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg",
            "jpeg",
            "png",
            "webp"
    );

    private final FileStorageProperties storageProperties;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    @Override
    public String storeProductImage(MultipartFile file) {

        validateImage(file);

        try {

            Path uploadPath = Paths.get(storageProperties.getProductDir());

            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String extension = getExtension(file.getOriginalFilename());

            String fileName = generateFileName(extension);

            Path destination = uploadPath.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return "/uploads/product/" + fileName;

        } catch (IOException ex) {

            throw new BadRequestException("Failed to store image.");

        }
    }

    @Override
    public void delete(String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        try {

            String fileName = Paths.get(imageUrl).getFileName().toString();

            Path filePath = Paths.get(
                    storageProperties.getProductDir(),
                    fileName
            );

            Files.deleteIfExists(filePath);

        } catch (IOException ex) {

            throw new BadRequestException("Failed to delete image.");

        }

    }

    /**
     * Validate uploaded image.
     */
    private void validateImage(MultipartFile file) {

    if (file == null || file.isEmpty()) {
        throw new BadRequestException("Image is required.");
    }

    // Kiểm tra MIME type
    String contentType = file.getContentType();

    if (contentType == null || !contentType.startsWith("image/")) {
        throw new BadRequestException("Invalid image file.");
    }

    // Kiểm tra dung lượng
    if (file.getSize() > MAX_FILE_SIZE) {
        throw new BadRequestException("Image size must not exceed 5 MB.");
    }

    // Kiểm tra extension
    String extension = getExtension(file.getOriginalFilename());

    if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
        throw new BadRequestException(
                "Only jpg, jpeg, png and webp are supported."
        );
    }
}

    /**
     * Get file extension.
     */
    private String getExtension(String fileName) {

        String cleanName = StringUtils.cleanPath(fileName);

        int index = cleanName.lastIndexOf('.');

        
        if (index < 0) {
            throw new BadRequestException("Invalid image file.");
        }

        return cleanName.substring(index + 1);

    }

    /**
     * Generate unique filename.
     */
    private String generateFileName(String extension) {

        return UUID.randomUUID() + "." + extension;

    }

}