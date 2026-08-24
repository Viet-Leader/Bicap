package com.bicap.dto.response.crop;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropResponse {

    private Long cropId;

    private String cropName;
}