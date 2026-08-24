package com.bicap.dto.request.productImage;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductImageRequest {

    @NotNull(message = "Image is required")
    private MultipartFile image;

    private Integer displayOrder;

}