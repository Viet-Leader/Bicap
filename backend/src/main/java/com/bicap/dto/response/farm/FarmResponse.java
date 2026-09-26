package com.bicap.dto.response.farm;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * DTO trả về thông tin Farm.
 */
@Data
@Builder
public class FarmResponse {

    /**
     * ID của Farm.
     */
    private Long farmId;

    /**
     * Tên trang trại.
     */
    private String farmName;

    /**
     * Giấy phép kinh doanh.
     */
    private String businessLicense;

    /**
     * Địa chỉ.
     */
    private String address;

    private String email;

    private BigDecimal areaSize;

    private String imageUrl;

    /**
     * Mô tả.
     */
    private String description;

    /**
     * Trạng thái Farm.
     */
    private String status;

    /**
     * Thời gian tạo.
     */
    private LocalDateTime createdAt;

    /**
     * Thời gian cập nhật gần nhất.
     */
    private LocalDateTime updatedAt;

}