package com.bicap.dto.request.productImage;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductImageRequest {

    @NotNull
    @Min(1)
    private Integer displayOrder;

}