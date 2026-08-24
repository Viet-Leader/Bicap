package com.bicap.dto.response.productImage;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageResponse {

    private Long imageId;

    private String imageUrl;

    private Integer displayOrder;

}